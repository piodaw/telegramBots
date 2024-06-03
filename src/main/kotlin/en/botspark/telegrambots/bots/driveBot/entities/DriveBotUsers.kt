package en.botspark.telegrambots.bots.driveBot.entities

import jakarta.persistence.*

@Entity
@Table(name = "drive_bot_users")
data class DriveBotUsers (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "telegram_user_id")
    var telegramUserId: Long? = null,

    @Column(name = "accepted_terms")
    var acceptedTerms: Boolean = false,

    @OneToMany(mappedBy = "driveBotUser", fetch = FetchType.LAZY)
    var project: Set<DriveBotChannels> = emptySet(),

    @OneToMany(mappedBy = "driveBotUser", fetch = FetchType.LAZY)
    var files: Set<DriveBotFiles> = emptySet()
)