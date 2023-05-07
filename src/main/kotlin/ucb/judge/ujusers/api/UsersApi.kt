package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.dto.ResponseDto
import ucb.judge.ujusers.dto.KeycloakUserDto

@RestController
@RequestMapping("/v1/api/users")
class UsersApi @Autowired constructor(private val usersBl: UsersBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }

    @GetMapping
    fun findAll():ResponseDto<List<KeycloakUserDto>>{
        logger.info("Starting the API call to find all users")
        val result: List<KeycloakUserDto> = usersBl.findAllUsers()
        logger.info("Finishing the API call to find all users")
        return ResponseDto(result)
    }

    @GetMapping("/username/{username}")
    fun findByUsername(@PathVariable username: String): ResponseDto<KeycloakUserDto> {
        logger.info("Starting the API call to find user by id")
        val result: KeycloakUserDto = usersBl.findByUsername(username)
        logger.info("Finishing the API call to find user by id")
        return ResponseDto(result)
    }

    @GetMapping("/{userId}")
    fun findById(@PathVariable userId: String): ResponseDto<KeycloakUserDto> {
        logger.info("Starting the API call to find user by id")
        val result: KeycloakUserDto = usersBl.findById(userId)
        logger.info("Finishing the API call to find user by id")
        return ResponseDto(result)
    }

    @PutMapping("/{userId}")
    fun update(@PathVariable userId: String,
               @RequestBody keycloakUserDto: KeycloakUserDto)
    : ResponseDto<KeycloakUserDto> {
        logger.info("Starting the API call to update user info")
        val result: KeycloakUserDto = usersBl.update(userId, keycloakUserDto)
        logger.info("Finishing the API call to update user info")
        return ResponseDto(result)
    }

    @PutMapping("/{userId}/password")
    fun resetPassword(@PathVariable userId: String,
                      @RequestBody userDto: UserDto)
    : ResponseDto<String> {
        logger.info("Starting the API call to update user password")
        usersBl.updatePassword(userId, userDto)
        logger.info("Finishing the API call to update user password")
        return ResponseDto("Password updated")
    }

    @DeleteMapping("/{userId}")
    fun delete(@PathVariable userId: String): ResponseDto<String> {
        logger.info("Starting the API call to delete user")
        usersBl.delete(userId)
        logger.info("Finishing the API call to delete user")
        return ResponseDto("User $userId deleted")
    }

    @PostMapping("/{userId}/group/{groupId}")
    fun assignToGroup(@PathVariable userId: String, @PathVariable groupId: String): ResponseDto<String> {
        logger.info("Starting the API call to assign user to group")
        usersBl.assignToGroup(userId, groupId)
        logger.info("Finishing the API call to assign user to group")
        return ResponseDto("User $userId assigned to group $groupId")
    }
}