package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujusers.dao.Major

interface MajorRepository : JpaRepository<Major, Long> {
    fun findAllByStatusIsTrueOrderByNameAsc(): List<Major>
}