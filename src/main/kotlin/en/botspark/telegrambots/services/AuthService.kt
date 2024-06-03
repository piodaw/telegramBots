package en.botspark.telegrambots.services

import en.botspark.telegrambots.config.EnvConfig
import en.botspark.telegrambots.dto.LoginDto
import en.botspark.telegrambots.dto.RegisterDto
import en.botspark.telegrambots.entities.Users
import en.botspark.telegrambots.enums.UserRoles
import en.botspark.telegrambots.repositories.UsersRepository
import en.botspark.telegrambots.utils.exceptions.ApiException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val usersRepository: UsersRepository,
    private val hashService: HashService,
    private val jwtService: JwtService,
    private val envConfig: EnvConfig
) {
    fun login(payload: LoginDto, response: HttpServletResponse): Users {
        val user = usersRepository.findByEmail(payload.email) ?: throw ApiException(400, "Login failed")

        if (!user.password?.let { hashService.checkBcrypt(payload.password, it) }!!) {
            throw ApiException(400, "Login failed")
        }

        if (!user.isEmailConfirmed) {
            throw ApiException(403, "Account is not verified")
        }

        if (!user.isActive) {
            throw ApiException(403, "Your account is banned")
        }

        response.addCookie(createTokenCookie(user))

        return user
    }

    fun register(payload: RegisterDto, response: HttpServletResponse): Users {
        if (usersRepository.findByEmail(payload.email) != null) {
            throw ApiException(400, "Email already exists")
        }

        if (payload.password != payload.confirmPassword) {
            throw ApiException(400, "Passwords are not equal")
        }

        if (usersRepository.findByUsername(payload.username) != null) {
            throw ApiException(400, "Username already exists")
        }

        val user = Users(
            email = payload.email,
            password = hashService.hashBcrypt(payload.password),
            role = UserRoles.USER,
            username = payload.username,
            region = payload.region,
            registrationDate = Date(),
            isActive = false
        )

        val savedUser = usersRepository.save(user)

        // @TODO: Send activation email

        response.addCookie(createTokenCookie(user))

        return savedUser
    }

    fun getMe(request: HttpServletRequest): Users {
        val token = jwtService.extractTokenFromCookies(request.cookies)
        val user = token?.let { jwtService.parseToken(it) } ?: throw ApiException(400, "Invalid token")

        return user
    }

    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("token", null).apply {
            isHttpOnly = true
            secure = envConfig.isProd
            path = "/"
            maxAge = 0
        }

        response.addCookie(cookie)
    }

    private fun createTokenCookie(user: Users): Cookie {
        return Cookie("token", jwtService.createToken(user)).apply {
            isHttpOnly = true
            secure = envConfig.isProd
            path = "/"
            maxAge = 60 * 60 * 2
        }
    }
}