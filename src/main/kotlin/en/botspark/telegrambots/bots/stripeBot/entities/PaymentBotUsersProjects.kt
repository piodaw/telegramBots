package en.botspark.telegrambots.bots.stripeBot.entities

import en.botspark.telegrambots.bots.stripeBot.enums.PaymentBotUserProjectPaymentTypes
import en.botspark.telegrambots.bots.stripeBot.enums.PaymentBotUserProjectTypes
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "payment_bot_users_projects")
data class PaymentBotUsersProjects (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_bot_user_id", nullable = false)
    var paymentBotUser: PaymentBotUsers? = null,

    @Column(name = "name")
    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: PaymentBotUserProjectTypes? = null,

    @Column(name = "telegram_project_chat_id")
    var telegramProjectChatId: Long? = null,

    @Column(name = "currency")
    var currency: String? = null,

    @Column(name = "amount")
    var amount: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_payment")
    var typeOfPayment: PaymentBotUserProjectPaymentTypes? = null,

    @Column(name = "subscription_period")
    var subscriptionPeriod: Int? = null,

    @Column(name = "project_bot_hashed_secret")
    var projectBotHashedSecret: String? = null,

    @Column(name = "created_at")
    var createdAt: Date? = null,

    @Column(name = "updated_at")
    var updatedAt: Date? = null,
)