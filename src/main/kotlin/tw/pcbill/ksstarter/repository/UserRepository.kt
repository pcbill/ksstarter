package tw.pcbill.ksstarter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tw.pcbill.ksstarter.model.User

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByUsername(username: String?) : User?

}