package en.botspark.telegrambots.repositories

import en.botspark.telegrambots.entities.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsersRepository: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?

    fun findById(id: UUID): Users?

    fun findByUsername(username: String): Users?
}