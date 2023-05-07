package ucb.judge.ujusers.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ucb.judge.ujusers.dto.ErrorResponseDto
import ucb.judge.ujusers.exception.UsersException
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException

@ControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDto("UJ-USERS: 0001", "Bad request"))
    }

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorizedException(ex: NotAuthorizedException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDto("UJ-USERS: 0002", "Not authorized"))
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseDto("UJ-USERS: 0003", "Forbidden"))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto("UJ-USERS: 0004", "Resource not found"))
    }

    @ExceptionHandler(UsersException::class)
    fun handleUJUsersException(ex: UsersException): ResponseEntity<ErrorResponseDto> {
        val code = when (ex.httpStatus) {
            HttpStatus.BAD_REQUEST -> "UJ-USERS: 0001"
            HttpStatus.NOT_FOUND -> "UJ-USERS: 0004"
            else -> "UJ-USERS: UNKNOWN"
        }
        return ResponseEntity.status(ex.httpStatus).body(ErrorResponseDto(code, ex.message!!))
    }
}