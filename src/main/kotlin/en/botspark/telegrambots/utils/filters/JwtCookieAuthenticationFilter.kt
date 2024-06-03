package en.botspark.telegrambots.utils.filters

import en.botspark.telegrambots.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

class JwtCookieAuthenticationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val cookie = WebUtils.getCookie(request, "token")
        cookie?.let {
            val jwt = it.value
            if (jwtService.validateToken(jwt)) {
                val claims = jwtService.parseToken(jwt)!!
                val authorities = claims.role?.let { SimpleGrantedAuthority(it.toString()) }?.let { listOf(it) }
                val authoritiesWithPrefix = authorities?.map { SimpleGrantedAuthority("ROLE_${it.authority}") }

                val authentication = UsernamePasswordAuthenticationToken(claims, null, authoritiesWithPrefix)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }
}