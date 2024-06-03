package en.botspark.telegrambots.entities

import com.fasterxml.jackson.annotation.JsonProperty
import en.botspark.telegrambots.enums.UserRoles
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "username", unique = true)
    var username: String? = null,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: UserRoles? = null,

    @Column(name = "registration_date")
    var registrationDate: Date? = null,

    @Column(name = "is_email_confirmed")
    var isEmailConfirmed: Boolean = false,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "region")
    var region: String? = null
)