package ucb.judge.ujusers.dto

data class ResponseDto<T>(
    val data: T?,
    val message: String,
    val successful: Boolean
);
