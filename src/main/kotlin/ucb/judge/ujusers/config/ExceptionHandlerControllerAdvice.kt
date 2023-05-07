package ucb.judge.ujusers.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import ucb.judge.ujusers.dto.ErrorResponseDto
import ucb.judge.ujusers.exception.KeycloakServiceException

@ControllerAdvice
class ExceptionHandlerControllerAdvice {
    companion object {
        private val logger = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice::class.java.name)
    }

    @ExceptionHandler(KeycloakServiceException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleBadRequest(e: KeycloakServiceException): ErrorResponseDto {
        logger.error(e.message)
        return ErrorResponseDto("UJ-USERS: Keycloak Service Error", e.message!!)
    }
}