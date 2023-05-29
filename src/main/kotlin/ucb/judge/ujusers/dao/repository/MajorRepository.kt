package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujusers.dao.Major

interface MajorRepository : JpaRepository<Major, Long> {
    fun findAllByStatusIsTrue(): List<Major>
    fun findByMajorIdAndStatusIsTrue(majorId: Long): Major?
}