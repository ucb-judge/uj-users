package ucb.judge.ujusers.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import ucb.judge.ujusers.dao.Campus
import ucb.judge.ujusers.dao.CampusMajor
import ucb.judge.ujusers.dao.Major
import ucb.judge.ujusers.dao.repository.CampusMajorRepository
import ucb.judge.ujusers.dao.repository.MajorRepository
import ucb.judge.ujusers.dto.CampusDto
import ucb.judge.ujusers.dto.MajorDto
import ucb.judge.ujusers.exception.UjNotFoundException
import ucb.judge.ujusers.mappers.CampusMapper
import ucb.judge.ujusers.mappers.MajorMapper

@Controller
class MajorsBl @Autowired constructor(
    private val majorRepository: MajorRepository,
    private val campusMajorRepository: CampusMajorRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    fun findAllMajors(): List<MajorDto> {
        logger.info("Starting the BL call to find all majors")
        val majors: List<Major> = majorRepository.findAllByStatusIsTrueOrderByNameAsc()
        logger.info("Found ${majors.size} campus")
        logger.info("Finishing the BL call to find all campus")
        return majors.map { MajorMapper.entityToDto(it) }
    }

    fun findAllByCampusId(campusId: Long): List<MajorDto> {
        logger.info("Starting the BL call to find all majors by campus id")
        val campusMajors: List<CampusMajor> = campusMajorRepository.findAllByCampusCampusIdAndStatusIsTrueOrderByCampusAsc(campusId)
        if (campusMajors.isEmpty()) {
            throw UjNotFoundException("Campus with id $campusId not found")
        }
        val majors: List<Major> = campusMajors.map { it.major!! }
        logger.info("Found ${majors.size} majors")
        logger.info("Finishing the BL call to find all majors by campus id")
        return majors.map { MajorMapper.entityToDto(it) }
    }
}