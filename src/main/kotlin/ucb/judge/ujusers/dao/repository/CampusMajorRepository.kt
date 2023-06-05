package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujusers.dao.CampusMajor

interface CampusMajorRepository : JpaRepository<CampusMajor, Long> {
    fun findAllByCampusCampusIdAndStatusIsTrueOrderByCampusAsc(campusId: Long): List<CampusMajor>

    fun findAllByMajorMajorIdAndStatusIsTrueOrderByMajorAsc(majorId: Long): List<CampusMajor>

    fun findByCampusMajorIdAndStatusIsTrueOrderByMajorNameAsc(campusMajorId: Long): CampusMajor?
}