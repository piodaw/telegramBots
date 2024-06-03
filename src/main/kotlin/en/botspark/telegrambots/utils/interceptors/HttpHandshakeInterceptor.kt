package en.botspark.telegrambots.utils.interceptors

import en.botspark.telegrambots.services.JwtService

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class HttpHandshakeInterceptor(
    private var jwtService: JwtService
): HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val cookies = request.headers["Cookie"]
        val jwtCookie = cookies
            ?.flatMap { it.split("; ") }
            ?.find { it.startsWith("token=") }
            ?.substringAfter("token=")

        jwtCookie?.let {
            val claims = jwtService.getExpirationTime(it)
            attributes["expiresAt"] = claims
        }

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // Implementation here...
    }
}
