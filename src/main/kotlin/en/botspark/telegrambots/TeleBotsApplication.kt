package en.botspark.telegrambots

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TeleBotsApplication

fun main(args: Array<String>) {
    runApplication<TeleBotsApplication>(*args)
}
