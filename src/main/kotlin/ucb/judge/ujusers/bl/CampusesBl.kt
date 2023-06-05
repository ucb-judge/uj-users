package ucb.judge.ujusers.bl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import ucb.judge.ujusers.dao.Campus
import ucb.judge.ujusers.dao.repository.CampusMajorRepository
import ucb.judge.ujusers.dao.repository.CampusRepository
import ucb.judge.ujusers.dto.CampusDto
import ucb.judge.ujusers.exception.UjNotFoundException
import ucb.judge.ujusers.mappers.CampusMapper


@Controller
class CampusesBl @Autowired constructor(
    private val campusRepository: CampusRepository,
    private val campusMajorRepository: CampusMajorRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(UsersBl::class.java.name)
    }

    fun findAllCampuses(): List<CampusDto> {
        logger.info("Starting the BL call to find all campus")
        val campuses: List<Campus> = campusRepository.findAllByStatusIsTrueOrderByNameAsc()
        logger.info("Found ${campuses.size} campus")
        logger.info("Finishing the BL call to find all campus")
        return campuses.map { CampusMapper.entityToDto(it) }
    }

    fun findAllByMajorId(majorId: Long): List<CampusDto> {
        logger.info("Starting the BL call to find all campus by major id")
        val campusMajors = campusMajorRepository.findAllByMajorMajorIdAndStatusIsTrueOrderByMajorAsc(majorId)
        val campuses: List<Campus> = campusMajors.map { it.campus!! }
        if (campusMajors.isEmpty()) {
            throw UjNotFoundException("Major with id $majorId not found")
        }
        logger.info("Found ${campuses.size} campus")
        logger.info("Finishing the BL call to find all campus by major id")
        return campuses.map { CampusMapper.entityToDto(it) }
    }
}