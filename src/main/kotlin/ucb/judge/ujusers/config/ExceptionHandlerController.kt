package ucb.judge.ujusers.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ucb.judge.ujusers.dto.ResponseDto
import ucb.judge.ujusers.exception.UsersException
import javax.ws.rs.*
import com.fasterxml.jackson.module.kotlin.readValue
import ucb.judge.ujusers.dto.KeycloakErrorDto


@ControllerAdvice
class ExceptionHandlerController {
    val objectMapper = jacksonObjectMapper()

    @ExceptionHandler(ClientErrorException::class)
    fun handleBadRequestException(ex: ClientErrorException): ResponseEntity<ResponseDto<Nothing>> {
        val httpStatus: HttpStatus = HttpStatus.valueOf(ex.response.status)
        val keycloakError: String = ex.response.readEntity(String::class.java)
        objectMapper.propertyNamingStrategy = com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
        val keycloakErrorDto: KeycloakErrorDto = objectMapper.readValue(keycloakError)
        val code = when (httpStatus) {
            HttpStatus.BAD_REQUEST -> "UJ-USERS: 0001"
            HttpStatus.UNAUTHORIZED -> "UJ-USERS: 0002"
            HttpStatus.FORBIDDEN -> "UJ-USERS: 0003"
            HttpStatus.NOT_FOUND -> "UJ-USERS: 0004"
            else -> "UJ-USERS: UNKNOWN"
        }
        return ResponseEntity.status(httpStatus).body(ResponseDto(code, keycloakErrorDto.errorDescription ?: keycloakErrorDto.error ?: "Unknown error"))
    }

    @ExceptionHandler(UsersException::class)
    fun handleUJUsersException(ex: UsersException): ResponseEntity<ResponseDto<Nothing>> {
        val code = when (ex.httpStatus) {
            HttpStatus.BAD_REQUEST -> "UJ-USERS: 0001"
            HttpStatus.NOT_FOUND -> "UJ-USERS: 0004"
            else -> "UJ-USERS: UNKNOWN"
        }
        return ResponseEntity.status(ex.httpStatus).body(ResponseDto(code, ex.message!!))
    }

}