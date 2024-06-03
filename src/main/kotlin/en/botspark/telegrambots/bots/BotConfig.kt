package en.botspark.telegrambots.bots

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BotConfig {
    @Value("\${STRIPE_TELEGRAM_BOT_TOKEN}")
    lateinit var paymentBotSecret: String

    @Value("\${DRIVE_TELEGRAM_BOT_TOKEN}")
    lateinit var driveBotSecret: String
}