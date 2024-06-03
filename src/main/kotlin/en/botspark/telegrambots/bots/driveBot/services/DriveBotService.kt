package en.botspark.telegrambots.bots.driveBot.services

import en.botspark.telegrambots.bots.driveBot.DriveBot
import en.botspark.telegrambots.bots.driveBot.entities.DriveBotFiles
import en.botspark.telegrambots.bots.driveBot.entities.DriveBotUsers
import en.botspark.telegrambots.bots.driveBot.repositories.DriveBotFilesRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DriveBotService(
    private val driveBot: DriveBot,
    private val driveBotFilesRepository: DriveBotFilesRepository
) {
    fun getFiles(): List<DriveBotFiles> {
        return driveBotFilesRepository.findAll()
    }

    fun sendFile(file: List<MultipartFile>) {
//        @TODO split file to chunks and send it to chat
//        println(file.size)
//        -1002201218813 test channel id
        val res = driveBot.sendFileToChannel("-1002201218813", file)
        res.forEach {
            DriveBotFiles(
                fileId = it.document.fileId,
                fileUniqueId = it.document.fileUniqueId,
                fileName = it.document.fileName,
                fileSize = it.document.fileSize,
                fileType = it.document.mimeType,
                uploadedAt = System.currentTimeMillis(),
                driveBotUser = DriveBotUsers(1L)
            ).apply {
                driveBotFilesRepository.save(this)
            }
        }
    }

    fun getFileFromDb(fileId: String): DriveBotFiles {
        return driveBotFilesRepository.findByFileId(fileId)
    }

    fun downloadFiles(filesIds: List<String>): List<String> {
        return driveBot.downloadFile(filesIds)
    }
}