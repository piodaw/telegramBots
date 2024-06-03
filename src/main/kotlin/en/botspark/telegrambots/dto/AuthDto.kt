package en.botspark.telegrambots.dto

import en.botspark.telegrambots.entities.Users

data class LoginDto (
    val email: String,
    val password: String
)

data class RegisterDto (
    val email: String,
    val password: String,
    val confirmPassword: String,
    val username: String,
    val region: String
)

data class LoginResponseDto (
    val user: Users
)