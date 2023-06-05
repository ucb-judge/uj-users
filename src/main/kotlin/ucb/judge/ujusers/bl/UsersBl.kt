package ucb.judge.ujusers.bl

import org.keycloak.OAuth2Constants.PASSWORD
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujusers.dao.Student
import ucb.judge.ujusers.dao.repository.CampusMajorRepository
import ucb.judge.ujusers.dao.repository.ProfessorRepository
import ucb.judge.ujusers.dao.repository.StudentRepository
import ucb.judge.ujusers.dto.KeycloakUserDto
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.exception.UjNotFoundException
import ucb.judge.ujusers.exception.UsersException
import ucb.judge.ujusers.utils.KeycloakSecurityContextHolder
import java.util.*
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@Service
class UsersBl @Autowired constructor(
    private val keycloak: Keycloak,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository,
    private val campusMajorRepository: CampusMajorRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    @Value("\${keycloak.auth-server-url}")
    private val authUrl: String? = null

    @Value("\${keycloak.realm}")
    private val realm: String? = null

    @Value("\${frontend-client-id}")
    private val frontendClientId: String? = null

    fun findAllUsers(): List<KeycloakUserDto> {
        logger.info("Starting the BL call to find all users")
        val users: List<UserRepresentation> = keycloak
                .realm(realm)
                .users()
                .list()
        logger.info("Found ${users.size} users")
        logger.info("Finishing the BL call to find all users")
        return users.map { convertToUserDto(it) }
    }

    fun findById(id: String): KeycloakUserDto {
        logger.info("Starting the BL call to find user by user id")
        val user: UserRepresentation = keycloak
            .realm(realm)
            .users()
            .get(id)
            .toRepresentation()
        logger.info("Finishing the BL call to find user by user id")
        return convertToUserDto(user)
    }

    fun createUser(userDto: UserDto,groupName: String) {
        logger.info("Starting the BL call to create user")
        // Input validation
        validatePasswordPolicy(userDto.password)
        if (groupName=="students" && userDto.campusMajorId==null) {
            throw UsersException(HttpStatus.BAD_REQUEST, "CampusMajorId is required")
        }
        val campusMajor = if (groupName=="students") campusMajorRepository.findById(userDto.campusMajorId!!).orElseThrow { UjNotFoundException("CampusMajor with id ${userDto.campusMajorId} not found") }
        else null

        val passwordRepresentation = preparePasswordRepresentation(userDto.password)
        val userRepresentation = prepareUserRepresentation(userDto, passwordRepresentation, groupName)

        val response:Response = keycloak
            .realm(realm)
            .users()
            .create(userRepresentation)
        if (response.status != 201) {
            throw ClientErrorException(response)
        }
        val userId = response.location.path.split("/").last()
        // Store user in database
        logger.info("Storing user in database")
        if (groupName=="students") {
            val student = Student()
            student.kcUuid = userId
            student.campusMajor = campusMajor
            studentRepository.save(student)
        } else {
            val professor = ucb.judge.ujusers.dao.Professor()
            professor.kcUuid = userId
            professorRepository.save(professor)
        }
        logger.info("User stored in database")
        logger.info("Finishing the BL call to create user")
    }

    fun update(userDto: UserDto): KeycloakUserDto {
        logger.info("Starting the BL call to update user info")
        val userId = KeycloakSecurityContextHolder.getSubject() ?: throw UsersException(HttpStatus.UNAUTHORIZED, "User id is required")
        val user: UserRepresentation = keycloak
            .realm(realm)
            .users()
            .get(userId)
            .toRepresentation()

        user.email = userDto.email ?: user.email
        user.firstName = userDto.firstName ?: user.firstName
        user.lastName = userDto.lastName ?: user.lastName

        // Verify email
        if (userDto.email != null && userDto.email != user.email) {
            logger.info("Sending email verification to ${userDto.email}")
            user.isEmailVerified = false
            user.requiredActions = listOf("VERIFY_EMAIL")
        }
        keycloak
            .realm(realm)
            .users()
            .get(userId)
            .update(user)
        logger.info("Finishing the BL call to update user info")
        return convertToUserDto(user)
    }

    fun updatePassword(userDto: UserDto) {
        logger.info("Starting the BL call to reset user password")
        if (userDto.currentPassword == null) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Current password is required")
        }
        checkCurrentPassword(userDto.currentPassword)
        val userId = KeycloakSecurityContextHolder.getSubject() ?: throw UsersException(HttpStatus.UNAUTHORIZED, "User id is required")
        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = userDto.password
        keycloak
            .realm(realm)
            .users()
            .get(userId)
            .resetPassword(credentialRepresentation)
        logger.info("Finishing the BL call to reset user password")
    }

    fun delete(userDto: UserDto) {
        logger.info("Starting the BL call to delete user")
        val userId = KeycloakSecurityContextHolder.getSubject() ?: throw UsersException(HttpStatus.UNAUTHORIZED, "User id is required")
        if (userDto.currentPassword == null) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Current password is required")
        }
        checkCurrentPassword(userDto.currentPassword)
//        Physically delete user from keycloak
//        keycloak
//            .realm(realm)
//            .users()
//            .delete(userId)

//        Soft delete user from keycloak
        val user: UserRepresentation = keycloak
            .realm(realm)
            .users()
            .get(userId)
            .toRepresentation()

        user.isEnabled = false
        keycloak
            .realm(realm)
            .users()
            .get(userId)
            .update(user)

        logger.info("User with id $userId deleted from keycloak")
        logger.info("Deleting user from database")
        val student = studentRepository.findByKcUuidAndStatusIsTrue(userId)
        student?.let {
            it.status = false
            studentRepository.save(it)
        }
        if (student != null) {
            logger.info("User with id ${student.kcUuid} deleted from database")
        }
        val professor = professorRepository.findByKcUuidAndStatusIsTrue(userId)
        professor?.let {
            it.status = false
            professorRepository.save(it)
        }
        if (professor != null) {
            logger.info("User with id ${professor.kcUuid} deleted from database")
        }
        logger.info("Finishing the BL call to delete user")
    }

    fun checkCurrentPassword(currentPassword: String) {
        logger.info("Starting the BL call to check current password")
        val username = KeycloakSecurityContextHolder.getUsername() ?: throw UsersException(HttpStatus.UNAUTHORIZED, "Username is required")
        val keycloak: Keycloak = KeycloakBuilder.builder()
            .grantType(PASSWORD)
            .serverUrl(authUrl)
            .realm(realm)
            .clientId(frontendClientId)
            .username(username)
            .password(currentPassword)
            .build()
        keycloak.tokenManager().accessToken
        logger.info("Current password is correct")
        logger.info("Finishing the BL call to check current password")
    }


    fun findByGroup(groupName: String): List<KeycloakUserDto> {
        logger.info("Starting the BL call to find users by group")
        val groups: List<GroupRepresentation> = keycloak.realm(realm)
            .groups()
            .groups()
            .filter { it.name == groupName }

        if (groups.isEmpty()) {
            logger.error("Group with name $groupName not found")
            throw UsersException(HttpStatus.NOT_FOUND, "Group with name $groupName not found")
        }
        logger.info("Found group with id ${groups[0].id}")

        val users = keycloak.realm(realm)
            .groups()
            .group(groups[0].id)
            .members()

        logger.info("Found ${users.size} users")
        logger.info("Finishing the BL call to find users by group")
        return users.map { convertToUserDto(it) }
    }

    fun convertToUserDto(userRepresentation: UserRepresentation): KeycloakUserDto {
        return KeycloakUserDto(
            userRepresentation.id,
            userRepresentation.username,
            userRepresentation.isEnabled,
            userRepresentation.isEmailVerified,
            userRepresentation.firstName,
            userRepresentation.lastName,
            userRepresentation.email?: "",
        )
    }

    private fun preparePasswordRepresentation(
        password: String?
    ): CredentialRepresentation {
        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = password
        return credentialRepresentation
    }
    private fun prepareUserRepresentation(
        userDto: UserDto,
        credentialRepresentation: CredentialRepresentation,
        groupName: String
    ): UserRepresentation {
        val userRepresentation = UserRepresentation()
        userRepresentation.username = userDto.username
        userRepresentation.email = userDto.email
        userRepresentation.isEnabled = true
        userRepresentation.isEmailVerified = false
        userRepresentation.firstName = userDto.firstName
        userRepresentation.lastName = userDto.lastName
        userRepresentation.credentials = listOf(credentialRepresentation)
        userRepresentation.groups = listOf(groupName)
        return userRepresentation
    }

    private fun validatePasswordPolicy (password: String?){
        // Check if password is null or empty
        if (password.isNullOrEmpty()) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Empty password not allowed")
        }
        // Check if password has at least 1 special character
        val regex = Regex("[^A-Za-z0-9 ]")
        if (!regex.containsMatchIn(password)) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Invalid password: must contain at least 1 special characters.")
        }
        // Check if password hast at least 1 uppercase letter
        val regex2 = Regex("[A-Z]")
        if (!regex2.containsMatchIn(password)) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Invalid password: must contain at least 1 upper case characters.")
        }
        // Check if password has at least 1 lowercase letter
        val regex3 = Regex("[a-z]")
        if (!regex3.containsMatchIn(password)) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Invalid password: must contain at least 1 lower case characters.")
        }
        // Check if password has length of at least 10 characters
        if (password.length < 10) {
            throw UsersException(HttpStatus.BAD_REQUEST, "Invalid password: minimum length 10.")
        }
    }

    fun getProfessorIdByKcUuid(kcUuid: String): Long {
        keycloak.realm(realm)
            .users()
            .get(kcUuid)
            .toRepresentation() ?: throw UjNotFoundException("Professor not found")
        return professorRepository.findByKcUuidAndStatusIsTrue(kcUuid)?.professorId
            ?: throw UjNotFoundException("Professor not found")
    }

    fun getStudentIdByKcUuid(kcUuid: String): Long {
        logger.info("Querying keycloak for user with uuid $kcUuid")
        keycloak.realm(realm)
            .users()
            .get(kcUuid)
            .toRepresentation() ?: throw UjNotFoundException("Student not found")
        logger.info("Querying database for student data")
        return studentRepository.findByKcUuidAndStatusIsTrue(kcUuid)?.studentId
            ?: throw UjNotFoundException("Student not found")
    }
}

