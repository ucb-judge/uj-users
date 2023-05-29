package ucb.judge.ujusers.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujusers.dao.Student

@Repository
interface StudentRepository: JpaRepository<Student, Long> {
    fun findByKcUuidAndStatusIsTrue(kcUuid: String): Student?
}