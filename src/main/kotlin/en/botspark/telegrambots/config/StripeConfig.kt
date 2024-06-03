package en.botspark.telegrambots.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class StripeConfig {
    @Value("\${STRIPE_PUBLIC}")
    lateinit var publicKey: String

    @Value("\${STRIPE_SECRET}")
    lateinit var secretKey: String

    @Value("\${STRIPE_SUCCESS_URL}")
    lateinit var success: String

    @Value("\${STRIPE_CANCEL_URL}")
    lateinit var cancel: String

    @Value("\${STRIPE_WEBHOOK_SECRET}")
    lateinit var webhookSecret: String
}