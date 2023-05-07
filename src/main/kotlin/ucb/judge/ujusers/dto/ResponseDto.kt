package ucb.judge.ujusers.dto

data class ResponseDto <T>(
    val statusCode: String,
    val response: T?,
    val errorDetail: String?,
){
    constructor(statusCode: String, message: String) : this(statusCode, null, message)
    constructor(response: T) : this("UJ-USERS: 0000", response, null)
}