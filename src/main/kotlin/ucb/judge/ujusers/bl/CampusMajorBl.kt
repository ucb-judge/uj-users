package ucb.judge.ujusers.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import ucb.judge.ujusers.dao.CampusMajor
import ucb.judge.ujusers.dao.repository.CampusMajorRepository
import ucb.judge.ujusers.dao.repository.StudentRepository
import ucb.judge.ujusers.dto.CampusMajorDto
import ucb.judge.ujusers.exception.UjNotFoundException
import ucb.judge.ujusers.mappers.CampusMajorMapper

@Controller
class CampusMajorBl @Autowired constructor(
    private val campusMajorRepository: CampusMajorRepository,
    private val studentRepository: StudentRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    fun findAllByCampusId(campusId: Long): List<CampusMajorDto> {
        logger.info("Starting the BL call to find all majors by campus id")
        val campusMajors: List<CampusMajor> = campusMajorRepository.findAllByCampusCampusIdAndStatusIsTrueOrderByCampusAsc(campusId)
        if (campusMajors.isEmpty()) {
            throw UjNotFoundException("Campus with id $campusId not found")
        }
        logger.info("Found ${campusMajors.size} campus-majors by campus id")
        return campusMajors.map { CampusMajorMapper.entityToDto(it) }
    }

    fun findCampusAndMajorFromKcUuid(kcUuid: String): CampusMajorDto {
        logger.info("Starting the BL call to find campus and major from kcUuid")
        val student = studentRepository.findByKcUuidAndStatusIsTrue(kcUuid)
            ?: throw UjNotFoundException("Student with kcUuid $kcUuid not found")

        val campusMajor: CampusMajor = campusMajorRepository.findByCampusMajorIdAndStatusIsTrueOrderByMajorNameAsc(student.campusMajor!!.campusMajorId)
            ?: throw UjNotFoundException("CampusMajor with id ${student.campusMajor!!.campusMajorId} not found")
        logger.info("Found campus and major from kcUuid")
        return CampusMajorMapper.entityToDto(campusMajor)
    }

}