package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujusers.bl.MajorsBl
import ucb.judge.ujusers.dto.CampusDto
import ucb.judge.ujusers.dto.MajorDto
import ucb.judge.ujusers.dto.ResponseDto

@Service
@RestController
@RequestMapping("/api/v1/majors")
class MajorsApi @Autowired constructor(private val majorsBl: MajorsBl) {
    companion object {
        private val logger = LoggerFactory.getLogger(UsersApi::class.java.name)
    }
    /**
     * This method is used to find all majors, no role is required
     * @return ResponseDto<List<MajorDto>>
     */
    @GetMapping()
    fun findAll(): ResponseEntity<ResponseDto<List<MajorDto>>>
    {
        logger.info("Starting the API call to find all majors")
        val result: List<MajorDto> = majorsBl.findAllMajors()
        logger.info("Finishing the API call to find all majors")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    /**
     * This method is used to find all majors by campus id, no role is required
     * @return ResponseDto<List<MajorDto>>
     */

    @GetMapping("/campus/{campusId}")
    fun findByCampusId(@PathVariable campusId: Long): ResponseEntity<ResponseDto<List<MajorDto>>>
    {
        logger.info("Starting the API call to find all majors by campus id")
        val result: List<MajorDto> = majorsBl.findAllByCampusId(campusId)
        logger.info("Finishing the API call to find all majors by campus id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }
}