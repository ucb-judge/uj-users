package ucb.judge.ujusers.dto

data class KeycloakUserDto (
    val id: String,
    val username: String?,
    val enabled: Boolean,
    val emailVerified: Boolean,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
