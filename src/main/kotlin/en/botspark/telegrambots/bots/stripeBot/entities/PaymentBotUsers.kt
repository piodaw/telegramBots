package en.botspark.telegrambots.bots.stripeBot.entities

import jakarta.persistence.*

@Entity
@Table(name = "payment_bot_users")
data class PaymentBotUsers (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "telegram_user_id")
    var telegramUserId: Long? = null,

    @Column(name = "accepted_terms")
    var acceptedTerms: Boolean = false,

    @OneToMany(mappedBy = "paymentBotUser", fetch = FetchType.LAZY)
    var project: Set<PaymentBotUsersProjects> = emptySet()
)