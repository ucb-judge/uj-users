package ucb.judge.ujusers.dao.repository
import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujusers.dao.Campus

interface CampusRepository : JpaRepository<Campus, Long> {
    fun findAllByStatusIsTrue(): List<Campus>
    fun findByCampusIdAndStatusIsTrue(campusId: Long): Campus?
}