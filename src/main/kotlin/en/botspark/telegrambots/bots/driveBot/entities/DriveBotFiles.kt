package en.botspark.telegrambots.bots.driveBot.entities

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.*

@Tag(name = "DriveBot Files", description = "DriveBot files API")
@Entity
@Table(name = "drive_bot_files")
data class DriveBotFiles(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "file_id")
    var fileId: String? = null,

    @Column(name = "file_unique_id")
    var fileUniqueId: String? = null,

    @Column(name = "file_name")
    var fileName: String? = null,

    @Column(name = "file_size")
    var fileSize: Long? = null,

    @Column(name = "file_type")
    var fileType: String? = null,

    @Column(name = "uploaded_at")
    var uploadedAt: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_bot_user_id", nullable = false)
    var driveBotUser: DriveBotUsers? = null,
)