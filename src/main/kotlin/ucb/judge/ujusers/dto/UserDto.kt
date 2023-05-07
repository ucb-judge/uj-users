package ucb.judge.ujusers.dto

data class UserDto (
    val id: String,
    val username: String,
    val enabled: Boolean,
    val emailVerified: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String
)
