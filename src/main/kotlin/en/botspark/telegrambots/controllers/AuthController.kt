package en.botspark.telegrambots.controllers

import en.botspark.telegrambots.dto.LoginDto
import en.botspark.telegrambots.dto.RegisterDto
import en.botspark.telegrambots.entities.Users
import en.botspark.telegrambots.services.AuthService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "Auth API")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody payload: LoginDto, response: HttpServletResponse): Users {
        return authService.login(payload, response)
    }

    @PostMapping("/register")
    fun register(@RequestBody payload: RegisterDto, response: HttpServletResponse): Users {
        return authService.register(payload, response)
    }

    @GetMapping("/me")
    fun me(request: HttpServletRequest): Users? {
        return authService.getMe(request)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {
        authService.logout(response)
    }
}