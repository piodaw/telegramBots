package en.botspark.telegrambots.bots.stripeBot.services

import en.botspark.telegrambots.bots.stripeBot.repositories.PaymentBotUsersProjectsRepository
import en.botspark.telegrambots.bots.stripeBot.dto.PaymentBotResponseDto
import en.botspark.telegrambots.bots.stripeBot.entities.PaymentBotUsers
import en.botspark.telegrambots.bots.stripeBot.entities.PaymentBotUsersProjects
import en.botspark.telegrambots.bots.stripeBot.enums.PaymentBotUserProjectTypes
import en.botspark.telegrambots.bots.stripeBot.repositories.PaymentBotUsersRepository
import en.botspark.telegrambots.utils.exceptions.ApiException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class PaymentBotService(
    private val restTemplate: RestTemplate,
    private val paymentBotUsersProjectsRepository: PaymentBotUsersProjectsRepository,
    private val paymentBotUsersRepository: PaymentBotUsersRepository
) {
    val token = "7007344150:AAErTaHci5USO7Lmduqc00SWfbij0_ICwWY"

    companion object {
        private const val TELEGRAM_API_URL = "https://api.telegram.org/bot%s/getMe"
    }

    fun checkIfUserAcceptedTerms(userId: Long): Boolean {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId)

        return user?.acceptedTerms ?: false
    }

    fun checkIfUserHasProjects(userId: Long): Boolean {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")

        val products = paymentBotUsersProjectsRepository.findByPaymentBotUser(user)

        return products.isNotEmpty()
    }

    fun createUser(userId: Long) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId)

        when (user) {
            null ->
                PaymentBotUsers(
                    telegramUserId = userId
                ).let {
                    paymentBotUsersRepository.save(it)
                }
            else -> return
        }
    }

    fun updateUserAcceptedTerms(userId: Long) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")

        user.acceptedTerms = true

        paymentBotUsersRepository.save(user)
    }

    fun getUserProjects(userId: Long): List<PaymentBotUsersProjects> {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")

        return paymentBotUsersProjectsRepository.findByPaymentBotUser(user)
    }

    fun getProject(projectId: Long): PaymentBotUsersProjects {
        return paymentBotUsersProjectsRepository.findById(projectId).orElseThrow()
    }

    fun createNewProject(userId: Long, projectName: String): PaymentBotUsersProjects {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")

        return PaymentBotUsersProjects(
            name = projectName,
            paymentBotUser = user,
            createdAt = Date()
        ).let {
            paymentBotUsersProjectsRepository.save(it)
        }
    }

    fun updateTelegramProjectChannelId(userId: Long, channelId: Long) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project = paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(404, "Project not found")

        project.telegramProjectChatId = channelId

        paymentBotUsersProjectsRepository.save(project)
    }

    fun updateTelegramProjectType(userId: Long, type: PaymentBotUserProjectTypes) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project = paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(404, "Project not found")

        project.type = type

        paymentBotUsersProjectsRepository.save(project)
    }

    fun updateTelegramProjectCurrency(userId: Long, currency: String) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project = paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(404, "Project not found")

        project.currency = currency

        paymentBotUsersProjectsRepository.save(project)
    }

    fun updateTelegramProjectPeriod(userId: Long, period: Int) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project =
            paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(
                404,
                "Project not found"
            )

        project.subscriptionPeriod = period

        paymentBotUsersProjectsRepository.save(project)
    }

    fun updateTelegramProjectBotToken(userId: Long, botToken: String) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project =
            paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(
                404,
                "Project not found"
            )

        // @TODO Add hashing

        project.projectBotHashedSecret = botToken

        paymentBotUsersProjectsRepository.save(project)
    }

    fun updateTelegramProjectPrice(userId: Long, price: Long) {
        val user = paymentBotUsersRepository.findByTelegramUserId(userId) ?: throw ApiException(404, "User not found")
        val project =
            paymentBotUsersProjectsRepository.findFirstByPaymentBotUserOrderByCreatedAtDesc(user) ?: throw ApiException(
                404,
                "Project not found"
            )

        project.amount = price

        paymentBotUsersProjectsRepository.save(project)
    }

    fun getBotInfoFromTelegramApi(): PaymentBotResponseDto? {
        val api = TELEGRAM_API_URL.format(token)

        return restTemplate.getForObject(api, PaymentBotResponseDto::class.java)
    }
}