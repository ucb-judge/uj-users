package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujusers.dao.Professor

@Repository
interface ProfessorRepository: JpaRepository<Professor, Long> {
    fun findByKcUuidAndStatusIsTrue(kcUuid: String): Professor?
}