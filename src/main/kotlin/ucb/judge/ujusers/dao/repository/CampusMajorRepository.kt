package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujusers.dao.CampusMajor

interface CampusMajorRepository : JpaRepository<CampusMajor, Long> {
    fun findAllByCampusCampusIdAndStatusIsTrue(campusId: Long): List<CampusMajor>

    fun findAllByMajorMajorIdAndStatusIsTrue(majorId: Long): List<CampusMajor>

    fun findByCampusMajorIdAndStatusIsTrue(campusMajorId: Long): CampusMajor?
}