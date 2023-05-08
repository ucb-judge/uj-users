package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.KeycloakUserDto
import ucb.judge.ujusers.dto.ResponseDto
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.utils.KeycloakSecurityContextHolder
import java.util.*
import javax.ws.rs.core.Response


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

    @PostMapping("/student")
    fun createStudent(@RequestBody userDto: UserDto): ResponseDto<String> {
        logger.info("Starting the API call to create student user")
        usersBl.createUser(userDto,"students")
        logger.info("Finishing the API call to create student user")
        return ResponseDto("Student user created successfully")
    }

//    @PostMapping("/professor")
//    fun createProfessor(@RequestBody userDto: UserDto): ResponseDto<KeycloakUserDto> {
//        logger.info("Starting the API call to create user")
//        val result: KeycloakUserDto = usersBl.createProfessor(userDto)
//        logger.info("Finishing the API call to create user")
//        return ResponseDto(result)
//    }

    @PutMapping("/{userId}")
    fun update(
        @PathVariable userId: String,
        @RequestBody userDto: UserDto,
    )
    : ResponseDto<KeycloakUserDto> {
        logger.info("Starting the API call to update user info")
        val id = KeycloakSecurityContextHolder.getId()
        //FIXME: GET ID FROM TOKEN
//        if (id != userId) {
//            logger.error("User $id is not authorized to update user $userId")
//            throw UsersException(HttpStatus.FORBIDDEN, "User $id is not authorized to update user $userId")
//        }
        val result: KeycloakUserDto = usersBl.update(userId, userDto)
        logger.info("Finishing the API call to update user info")
        return ResponseDto(result)
    }

    @GetMapping("/me")
    fun text():String {
        val id = KeycloakSecurityContextHolder.getId()
        return id.toString()
    }

    @PutMapping("/{userId}/password")
    fun resetPassword(@PathVariable userId: String,
                      @RequestBody userDto: UserDto
    )
    : ResponseDto<String> {
        logger.info("Starting the API call to update user password")
        val id = KeycloakSecurityContextHolder.getId()
        //FIXME: GET ID FROM TOKEN
//        if (id != userId) {
//            logger.error("User $id is not authorized to update user $userId")
//            throw UsersException(HttpStatus.FORBIDDEN, "User $id is not authorized to update user $userId")
//        }
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

    @GetMapping("/group/{groupName}")
    fun findByGroup(@PathVariable groupName: String): ResponseDto<List<KeycloakUserDto>> {
        logger.info("Starting the API call to find all users by group")
        val result: List<KeycloakUserDto> = usersBl.findByGroup(groupName)
        logger.info("Finishing the API call to find all users by group")
        return ResponseDto(result)
    }

}