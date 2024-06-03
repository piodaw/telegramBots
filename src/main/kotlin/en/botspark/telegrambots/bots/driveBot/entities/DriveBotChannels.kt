package en.botspark.telegrambots.bots.driveBot.entities

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.*
import java.util.*

@Tag(name = "DriveBot Channels", description = "DriveBot channels API")
@Entity
@Table(name = "drive_bot_channels")
data class DriveBotChannels(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_bot_user_id", nullable = false)
    var driveBotUser: DriveBotUsers? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "telegram_channel_id")
    var telegramChannelId: Long? = null,

    @Column(name = "hashed_bot_secret")
    var hashedBotSecret: String? = null,

    @Column(name = "created_at")
    var createdAt: Date? = null,

    @Column(name = "updated_at")
    var updatedAt: Date? = null,
)