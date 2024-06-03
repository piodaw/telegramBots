package en.botspark.telegrambots.services

import en.botspark.telegrambots.entities.Users
import jakarta.servlet.http.Cookie
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class JwtService(
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val userService: UserService
) {
    fun createToken(user: Users): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60 * 60 * 2))
            .subject(user.email)
            .claim("userId", user.id)
            .claim("role", user.role)
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun parseToken(token: String): Users? {
        return try {
            val jwt = jwtDecoder.decode(token)
            val userId = jwt.claims["userId"] as String
            userService.findByUUID(UUID.fromString(userId))
        } catch (e: Exception) {
            null
        }
    }

    fun getExpirationTime(token: String): Long {
        val jwt = jwtDecoder.decode(token)
        val expirationTime = jwt.expiresAt
        if (expirationTime != null) {
            return expirationTime.epochSecond
        }
        return 0
    }

    fun validateToken(token: String): Boolean {
        return try {
            jwtDecoder.decode(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractTokenFromCookies(cookies: Array<Cookie>?): String? {
        return cookies?.find { it.name == "token" }?.value
    }
}