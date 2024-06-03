package en.botspark.telegrambots.bots.stripeBot.repositories

import en.botspark.telegrambots.bots.stripeBot.entities.PaymentBotUsers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentBotUsersRepository: JpaRepository<PaymentBotUsers, Long> {
    fun findByTelegramUserId(telegramUserId: Long): PaymentBotUsers?
}