package ucb.judge.ujusers.dto

data class UserDto (
    val username: String ?,
    val currentPassword: String ?,
    val password: String ?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val campusMajorId: Long?
)