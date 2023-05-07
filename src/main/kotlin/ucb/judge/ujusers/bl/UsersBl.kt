package ucb.judge.ujusers.bl


import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.exception.UsersException


@Service
class UsersBl @Autowired constructor(private val keycloak: Keycloak) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    @Value("\${keycloak.realm}")
    private val realm: String? = null

    fun findAllUsers(): List<UserDto> {
        logger.info("Starting the BL call to find all users")
        val users: List<UserRepresentation> = keycloak
                .realm(realm)
                .users()
                .list()
        logger.info("Found ${users.size} users")
        logger.info("Finishing the BL call to find all users")
        return users.map { convertToUserDto(it) }
    }

    fun findByUsername(username: String): UserDto {
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

    fun convertToUserDto(userRepresentation: UserRepresentation): UserDto {
        return UserDto(
            userRepresentation.id,
            userRepresentation.username,
            userRepresentation.isEnabled,
            userRepresentation.isEmailVerified,
            userRepresentation.firstName,
            userRepresentation.lastName,
            userRepresentation.email,
        )
    }
}