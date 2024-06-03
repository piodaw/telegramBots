package en.botspark.telegrambots.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class EnvConfig {
    @Value("\${IS_PROD}")
    var isProd: Boolean = false
}