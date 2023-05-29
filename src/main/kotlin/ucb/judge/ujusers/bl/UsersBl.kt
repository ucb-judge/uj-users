package ucb.judge.ujusers.bl


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujusers.dao.repository.ProfessorRepository
import ucb.judge.ujusers.dao.repository.StudentRepository
import ucb.judge.ujusers.dto.EmailDto
import ucb.judge.ujusers.dto.KeycloakUserDto
import ucb.judge.ujusers.dto.NotificationDto
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.exception.UjNotFoundException
import ucb.judge.ujusers.exception.UsersException
import ucb.judge.ujusers.producer.NotificationProducer
import java.util.*
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@Service
class UsersBl @Autowired constructor(
    private val keycloak: Keycloak,
    private val notificationProducer: NotificationProducer,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    @Value("\${keycloak.realm}")
    private val realm: String? = null

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

    fun findByUsername(username: String): KeycloakUserDto {
        logger.info("Starting the BL call to find user by username")
        val user: List<UserRepresentation> = keycloak
                .realm(realm)
                .users()
                .search(username)

        if (user.isEmpty()) {
            logger.error("User with username $username not found")
            throw UsersException(HttpStatus.NOT_FOUND, "User with username $username not found")
        }
        logger.info("Finishing the BL call to find user by username")
        return convertToUserDto(user[0])
    }

    fun findById(id: String): KeycloakUserDto {
        logger.info("Starting the BL call to find user by username")
        val user: UserRepresentation = keycloak
            .realm(realm)
            .users()
            .get(id)
            .toRepresentation()
        logger.info("Finishing the BL call to find user by username")
        return convertToUserDto(user)
    }

    fun createUser(userDto: UserDto,groupName: String) {
        logger.info("Starting the BL call to create user")
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
        logger.info("User created with id $userId")
        // sending email
        logger.info("Sending email to ${userDto.email}")
        val emailDto = EmailDto(userDto.email!!, "Bienvenido a UCB-JUDGE", "Hola ${userDto.firstName} ${userDto.lastName},\n\n" +
                "Tu cuenta ha sido creada exitosamente. \n\n" +
                "Si no has creado una cuenta en UCB-JUDGE, por favor ignora este correo.\n\n" +
                "Saludos,\n" +
                "El equipo de UCB-JUDGE")
        val objectMapper = jacksonObjectMapper()
        val emailString = objectMapper.writeValueAsString(emailDto)
        val notificationDto = NotificationDto(emailString, "Email", Date())
        logger.info("Sending Notification")
        notificationProducer.sendNotification(notificationDto)
        logger.info("Notification sent")
        // TODO: ADD USER_ID TO DATABASE
        logger.info("Finishing the BL call to create user")
    }

    fun update(userId: String, userDto: UserDto): KeycloakUserDto {
        logger.info("Starting the BL call to update user info")
        val user: UserRepresentation = keycloak
            .realm(realm)
            .users()
            .get(userId)
            .toRepresentation()

        user.email = userDto.email ?: user.email
        user.firstName = userDto.firstName ?: user.firstName
        user.lastName = userDto.lastName ?: user.lastName
        // Verify email
        if (userDto.email != null) {
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

    fun updatePassword(userId: String, userDto: UserDto) {
        logger.info("Starting the BL call to reset user password")
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

    fun delete(userId: String) {
        logger.info("Starting the BL call to delete user")
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
        logger.info("Finishing the BL call to delete user")
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
            userRepresentation.email,
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
