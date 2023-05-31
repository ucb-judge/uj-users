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
import org.slf4j.LoggerFactory
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.KeycloakErrorDto
import ucb.judge.ujusers.exception.UjNotFoundException


@ControllerAdvice
class ExceptionHandlerController {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandlerController::class.java.name)
    }

    val objectMapper = jacksonObjectMapper()

    @ExceptionHandler(ClientErrorException::class)
    fun handleBadRequestException(ex: ClientErrorException): ResponseEntity<ResponseDto<Nothing>> {
        var httpStatus: HttpStatus = HttpStatus.valueOf(ex.response.status)
        val keycloakError: String = ex.response.readEntity(String::class.java)
        if (keycloakError.contains("errorMessage")){
            logger.error("Error message: $keycloakError")
            httpStatus = HttpStatus.BAD_REQUEST
            objectMapper.propertyNamingStrategy = com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE
        } else {
            logger.error("Error message: $keycloakError")
            httpStatus = HttpStatus.BAD_REQUEST
            objectMapper.propertyNamingStrategy = com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
        }
        val keycloakErrorDto: KeycloakErrorDto = objectMapper.readValue(keycloakError)
        return ResponseEntity(ResponseDto(
            null,
            keycloakErrorDto.errorDescription ?: keycloakErrorDto.error ?: keycloakErrorDto.errorMessage ?: "Unknown error",
            false
        ), httpStatus)
    }


    @ExceptionHandler(UsersException::class)
    fun handleUJUsersException(ex: UsersException): ResponseEntity<ResponseDto<Nothing>> {
        logger.error("Error message: ${ex.message}")
        return ResponseEntity(ResponseDto(null, ex.message!!, false), ex.httpStatus)
    }

    @ExceptionHandler(UjNotFoundException::class)
    fun handleUjNotFoundException(ex: UjNotFoundException): ResponseEntity<ResponseDto<Nothing>> {
        return ResponseEntity(ResponseDto(null, ex.message!!, false), HttpStatus.NOT_FOUND)
    }
}