package ucb.judge.ujusers.exception

import org.springframework.http.HttpStatus


class UsersException(var httpStatus: HttpStatus, message: String) : Exception(message)