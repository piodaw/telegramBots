package en.botspark.telegrambots.bots.stripeBot.repositories

import en.botspark.telegrambots.bots.stripeBot.entities.PaymentBotUsers
import en.botspark.telegrambots.bots.stripeBot.entities.PaymentBotUsersProjects
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentBotUsersProjectsRepository: JpaRepository<PaymentBotUsersProjects, Long> {
    fun findByPaymentBotUser(paymentBotUser: PaymentBotUsers): List<PaymentBotUsersProjects>

    fun findFirstByPaymentBotUserOrderByCreatedAtDesc(paymentBotUser: PaymentBotUsers): PaymentBotUsersProjects?
}