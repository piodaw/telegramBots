package en.botspark.telegrambots.config

import en.botspark.telegrambots.services.JwtService
import en.botspark.telegrambots.utils.filters.JwtCookieAuthenticationFilter
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    @Autowired
    private lateinit var jwtService: JwtService

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            addFilterBefore(JwtCookieAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter::class.java)
            cors {  }
            authorizeRequests {
                authorize("/api/auth/me", authenticated)
                authorize("/api/auth/**", permitAll)
                authorize("/api/stripe/webhook", permitAll)
                authorize("/api/admin/**", hasRole("ADMIN"))
                authorize("/api/bots/**", permitAll)
                authorize("/api/**", authenticated)
            }
            csrf {
                disable()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
        return http.build()
    }
}