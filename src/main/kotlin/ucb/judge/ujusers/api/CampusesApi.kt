package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujusers.bl.CampusesBl
import ucb.judge.ujusers.dto.CampusDto
import ucb.judge.ujusers.dto.ResponseDto

@Service
@RestController
@RequestMapping("/api/v1/campuses")
class CampusesApi @Autowired constructor(private val campusesBl: CampusesBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }
    /**
     * This method is used to find all campuses, no role is required
     * @return ResponseDto<List<CampusDto>>
     */
    @GetMapping()
    fun findAll(): ResponseEntity<ResponseDto<List<CampusDto>>>
    {
        logger.info("Starting the API call to find all campus")
        val result: List<CampusDto> = campusesBl.findAllCampuses()
        logger.info("Finishing the API call to find all campus")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to find all campuses by major id, no role is required
     * @return ResponseDto<List<CampusDto>>
     */
    @GetMapping("/majors/{majorId}")
    fun findByMajorId(@PathVariable majorId: Long): ResponseEntity<ResponseDto<List<CampusDto>>>
    {
        logger.info("Starting the API call to find all campus by major id")
        val result: List<CampusDto> = campusesBl.findAllByMajorId(majorId)
        logger.info("Finishing the API call to find all campus by major id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }
}