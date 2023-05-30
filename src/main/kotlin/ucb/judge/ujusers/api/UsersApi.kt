package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.KeycloakUserDto
import ucb.judge.ujusers.dto.ResponseDto
import ucb.judge.ujusers.dto.UserDto
import ucb.judge.ujusers.exception.UsersException
import ucb.judge.ujusers.utils.KeycloakSecurityContextHolder
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UsersApi @Autowired constructor(private val usersBl: UsersBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }

    /**
     * This method is used to find all users
     * @return ResponseDto<List<KeycloakUserDto>>
     */
    @GetMapping()
    fun findAll(): ResponseEntity<ResponseDto<List<KeycloakUserDto>>> {
        logger.info("Starting the API call to find all users")
        val result: List<KeycloakUserDto> = usersBl.findAllUsers()
        logger.info("Finishing the API call to find all users")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to find user by username
     * @param username
     * @return ResponseDto<KeycloakUserDto>
     */

    @GetMapping("/profile/username/{username}")
    fun findByUsername(@PathVariable username: String): ResponseEntity<ResponseDto<KeycloakUserDto>> {
        logger.info("Starting the API call to find user by id")
        val result: KeycloakUserDto = usersBl.findByUsername(username)
        logger.info("Finishing the API call to find user by id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to find user by id
     * @param userId
     * @return ResponseDto<KeycloakUserDto>
     */
    @GetMapping("/profile/{userId}")
    fun findById(@PathVariable userId: String): ResponseEntity<ResponseDto<KeycloakUserDto>> {
        logger.info("Starting the API call to find user by id")
        val result: KeycloakUserDto = usersBl.findById(userId)
        logger.info("Finishing the API call to find user by id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to update user info
     * @param userId
     * @param userDto
     * @return ResponseDto<KeycloakUserDto>
     */
    @PutMapping("/profile/{userId}")
    fun update(
        @PathVariable userId: String,
        @RequestBody userDto: UserDto,
    )
    : ResponseEntity<ResponseDto<KeycloakUserDto>> {
        logger.info("Starting the API call to update user info")
        val id = KeycloakSecurityContextHolder.getId()
        if (id != userId) {
            logger.error("User $id is not authorized to update user $userId")
            throw UsersException(HttpStatus.FORBIDDEN, "User $id is not authorized to update user $userId")
        }
        val result: KeycloakUserDto = usersBl.update(userId, userDto)
        logger.info("Finishing the API call to update user info")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to update user password
     * @param userId
     * @param userDto
     * @return ResponseDto<String>
     */
    @PutMapping("/profile/{userId}/password")
    fun resetPassword(@PathVariable userId: String,
                      @RequestBody userDto: UserDto
    ) : ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to update user password")
        val id = KeycloakSecurityContextHolder.getId()
        if (id != userId) {
            logger.error("User $id is not authorized to update user $userId")
            throw UsersException(HttpStatus.FORBIDDEN, "User $id is not authorized to update user $userId")
        }
        usersBl.updatePassword(userId, userDto)
        logger.info("Finishing the API call to update user password")
        return ResponseEntity.ok(ResponseDto("Password updated","",true))
    }

    /**
     * This method is used to delete a user
     * @param userId
     * @return ResponseDto<String>
     */
    @DeleteMapping("/profile/{userId}")
    fun delete(@PathVariable userId: String): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to delete user")
        val id = KeycloakSecurityContextHolder.getId()
        if (id != userId) {
            logger.error("User $id is not authorized to update user $userId")
            throw UsersException(HttpStatus.FORBIDDEN, "User $id is not authorized to update user $userId")
        }
        usersBl.delete(userId)
        logger.info("Finishing the API call to delete user")
        return ResponseEntity.ok(ResponseDto("User deleted","", true))
    }

    /**
        * This method is used to create a student user
        * @param userDto
        * @return ResponseDto<String>
     */
    @PostMapping("/student")
    fun createStudent(@RequestBody userDto: UserDto): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to create student user")
        usersBl.createUser(userDto,"students")
        logger.info("Finishing the API call to create student user")
        return ResponseEntity.ok(ResponseDto("Student user created successfully", "", true))
    }

    /**
        * This method is used to create a professor user
        * @param userDto
        * @return ResponseDto<String>
     */
    @PostMapping("/professor")
    fun createProfessor(@RequestBody userDto: UserDto): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to create student user")
        usersBl.createUser(userDto,"professors")
        logger.info("Finishing the API call to create student user")
        return ResponseEntity.ok(ResponseDto("Professor user created successfully", "", true))
    }


    /**
     * This method is used to find all users by group
     * @param groupName
     * @return ResponseDto<List<KeycloakUserDto>>
     */
    @GetMapping("/group/{groupName}")
    fun findByGroup(@PathVariable groupName: String): ResponseEntity<ResponseDto<List<KeycloakUserDto>>> {
        logger.info("Starting the API call to find all users by group")
        val result: List<KeycloakUserDto> = usersBl.findByGroup(groupName)
        logger.info("Finishing the API call to find all users by group")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to check if a professor exists.
     * @param kcUuid professor's keycloak uuid
     * @return ResponseDto<Boolean>
     */
    @GetMapping("/professors")
    fun getProfessorIdByKcUuid(
        @RequestParam("kcUuid", required = true) kcUuid: String
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to verify if professor exists")
        val result: Long = usersBl.getProfessorIdByKcUuid(kcUuid)
        logger.info("Finishing the API call to verify if professor exists")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to check if a student exists.
     * @param kcUuid student's keycloak uuid
     * @return ResponseDto<Boolean>
     */
    @GetMapping("/students")
    fun getStudentIdByKcUuid(
        @RequestParam("kcUuid", required = true) kcUuid: String
    ): ResponseEntity<ResponseDto<Long>> {
        logger.info("Starting the API call to verify if student exists")
        val result: Long = usersBl.getStudentIdByKcUuid(kcUuid)
        logger.info("Finishing the API call to verify if student exists")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }
}