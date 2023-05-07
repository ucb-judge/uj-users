package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.UserDto

@RestController
@RequestMapping("/v1/api/users")
class UsersApi @Autowired constructor(private val usersBl: UsersBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }

    @GetMapping
    fun findAll():List<UserDto>{
        logger.info("Starting the API call to find all users")
        val result: List<UserDto> = usersBl.findAllUsers()
        logger.info("Finishing the API call to find all users")
        return result
    }

    @GetMapping("/username/{username}")
    fun findByUsername(@PathVariable username: String): UserDto {
        logger.info("Starting the API call to find user by id")
        val result: UserDto = usersBl.findByUsername(username)
        logger.info("Finishing the API call to find user by id")
        return result
    }
}