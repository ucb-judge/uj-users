package ucb.judge.ujusers.bl


import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ucb.judge.ujusers.dto.UserDto
import javax.ws.rs.core.Response


@Service
class UsersBl @Autowired constructor(private val keycloak: Keycloak) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    @Value("\${keycloak.realm}")
    private val realm: String? = null

    fun findAllUsers(): List<UserDto> {
        val users : List<UserRepresentation> =  keycloak
            .realm(realm)
            .users()
            .list()
        return users.map { convertToUserDto(it) }
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