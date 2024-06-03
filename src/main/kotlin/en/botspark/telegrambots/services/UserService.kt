package en.botspark.telegrambots.services

import en.botspark.telegrambots.entities.Users
import en.botspark.telegrambots.repositories.UsersRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val usersRepository: UsersRepository
) {
    fun findByUUID(uuid: UUID): Users? {
        return usersRepository.findById(uuid)
    }

    fun findByEmail(email: String): Users? {
        return usersRepository.findByEmail(email)
    }

    fun save(user: Users) {
        usersRepository.save(user)
    }
}