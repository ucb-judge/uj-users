package ucb.judge.ujusers.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ucb.judge.ujusers.bl.CampusMajorBl
import ucb.judge.ujusers.dto.CampusMajorDto
import ucb.judge.ujusers.dto.ResponseDto

@Service
@RestController
@RequestMapping("/api/v1/campuses-majors")
class CampusMajorApi @Autowired constructor(private val campusMajorBl: CampusMajorBl) {

    companion object {
        private val logger = LoggerFactory.getLogger(CampusMajorApi::class.java.name)
    }

    @GetMapping("/campus/{campusId}")
    fun findAllMajorsByCampusId(@PathVariable campusId: Long): ResponseEntity<ResponseDto<List<CampusMajorDto>>> {
        logger.info("Starting the API call to find all majors by campus id")
        val result: List<CampusMajorDto> = campusMajorBl.findAllByCampusId(campusId)
        logger.info("Finishing the API call to find all majors by campus id")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

    @GetMapping("/student/{kcUuid}")
    fun findCampusAndMajorFromKcUuid(@PathVariable kcUuid: String): ResponseEntity<ResponseDto<CampusMajorDto>> {
        logger.info("Starting the API call to find campus and major from kcUuid")
        val result: CampusMajorDto = campusMajorBl.findCampusAndMajorFromKcUuid(kcUuid)
        logger.info("Finishing the API call to find campus and major from kcUuid")
        return ResponseEntity.ok(ResponseDto(result, "", true))
    }

}