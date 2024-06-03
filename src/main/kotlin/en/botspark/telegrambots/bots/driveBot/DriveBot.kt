package en.botspark.telegrambots.bots.driveBot

import en.botspark.telegrambots.bots.BotConfig
import en.botspark.telegrambots.bots.driveBot.services.DriveBotService
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.Date
import java.util.UUID

@Component
class DriveBot(
    private val botConfig: BotConfig
) : SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final val telegramClient: TelegramClient

    init {
        telegramClient = OkHttpTelegramClient(botToken)
    }

    override fun getBotToken(): String {
        return botConfig.driveBotSecret
    }

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer {
        return this
    }

    override fun consume(update: Update?) {
        println(update)
        if (update!!.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId
        }
    }

    // send message when driveBotService.sendFile is called
    fun sendFileToChannel(chatId: String, files: List<MultipartFile>): List<Message> {
        return files.map { sendFile(chatId, it) }
    }

    fun downloadFile(filesIds: List<String>): List<String> {
        return filesIds.map { telegramClient.execute(GetFile.builder().fileId(it).build()) }
            .map { "https://api.telegram.org/file/bot$botToken/${it.filePath}" }
    }

    private fun sendFile(chatId: String, file: MultipartFile): Message {
        val inputFile = InputFile(file.inputStream, file.originalFilename)
        val fileMessage = SendDocument
            .builder()
            .chatId(chatId)
            .document(inputFile)
            .caption("File: ${file.originalFilename}")
            .thumbnail(inputFile)
            .build()
        return telegramClient.execute(fileMessage)
    }
}