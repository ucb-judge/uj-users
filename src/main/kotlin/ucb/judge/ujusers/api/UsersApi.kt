package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ucb.judge.ujusers.bl.UsersBl
import ucb.judge.ujusers.dto.KeycloakUserDto
import ucb.judge.ujusers.dto.ResponseDto
import ucb.judge.ujusers.dto.UserDto

import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UsersApi @Autowired constructor(private val usersBl: UsersBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }

// Get all users related methods
    /**
     * This method is used to find all users, uj-users-access role is required
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
     * This method is used to find all users by group, uj-users-access role is required
     * @param groupName
     * @return ResponseDto<List<KeycloakUserDto>>
     */
    @GetMapping("/group/{groupName}")
    fun findAllByGroupName(@PathVariable groupName: String): ResponseEntity<ResponseDto<List<KeycloakUserDto>>> {
        logger.info("Starting the API call to find all users by group")
        val result: List<KeycloakUserDto> = usersBl.findByGroup(groupName)
        logger.info("Finishing the API call to find all users by group")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

// Create user related methods
    /**
     * This method is used to create a student user, no role is required
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
     * This method is used to create a professor user, uj-users-access role is required
     * @param userDto
     * @return ResponseDto<String>
     */
    @PostMapping("/professor")
    fun createProfessor(@RequestBody userDto: UserDto): ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to create professor user")
        usersBl.createUser(userDto,"professors")
        logger.info("Finishing the API call to create professor user")
        return ResponseEntity.ok(ResponseDto("Professor user created successfully", "", true))
    }

// CRUD profile related methods
    /**
     * This method is used to find user by id, common-user role or uj-users-access role is required
     * @param userId
     * @return ResponseDto<KeycloakUserDto>
     */
    @GetMapping("/profile/{userId}")
    fun findByByKcUuid(@PathVariable userId: String): ResponseEntity<ResponseDto<KeycloakUserDto>> {
        logger.info("Starting the API call to find user by id")
        val result: KeycloakUserDto = usersBl.findById(userId)
        logger.info("Finishing the API call to find user by id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to update user info, is expected that token is sent in the header, common-user role or uj-users-access role is required
     * @param userDto
     * @return ResponseDto<KeycloakUserDto>
     */
    @PutMapping("/profile")
    fun update(@RequestBody userDto: UserDto): ResponseEntity<ResponseDto<KeycloakUserDto>> {
        logger.info("Starting the API call to update user info")
        val result: KeycloakUserDto = usersBl.update(userDto)
        logger.info("Finishing the API call to update user info")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to update user password, is expected that token is sent in the header, common-user role or uj-users-access role is required
     * @param userDto
     * @return ResponseDto<String>
     */
    @PutMapping("/profile/password")
    fun resetPassword(@RequestBody userDto: UserDto) : ResponseEntity<ResponseDto<String>> {
        logger.info("Starting the API call to update user password")
        usersBl.updatePassword(userDto)
        logger.info("Finishing the API call to update user password")
        return ResponseEntity.ok(ResponseDto("Password updated","",true))
    }

    /**
     * This method is used to delete a user, is expected that token is sent in the header, common-user role or uj-users-access role is required
     * @return ResponseDto<String>
     */
    @DeleteMapping("/profile")
    fun delete(@RequestBody userDto: UserDto): ResponseEntity<ResponseDto<String>> {
        usersBl.delete(userDto)
        logger.info("Finishing the API call to delete user")
        return ResponseEntity.ok(ResponseDto("User deleted","", true))
    }



    /**
     * This method is used to check if a professor exists, uj-users-access role is required
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
     * This method is used to check if a student exists, uj-users-access role is required
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