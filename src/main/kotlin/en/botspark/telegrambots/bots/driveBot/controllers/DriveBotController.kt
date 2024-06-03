package en.botspark.telegrambots.bots.driveBot.controllers

import en.botspark.telegrambots.bots.driveBot.entities.DriveBotFiles
import en.botspark.telegrambots.bots.driveBot.services.DriveBotService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.HttpURLConnection
import java.net.URI
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@Tag(name = "DriveBot", description = "DriveBot API")
@RestController
@RequestMapping("/api/bots/drive-bot")
class DriveBotController(
    private val driveBotService: DriveBotService
) {
    @GetMapping("/get-files")
    fun getFiles(): List<DriveBotFiles> {
        return driveBotService.getFiles()
    }

    @GetMapping("/get-file/{fileId}")
    fun getFile(@PathVariable fileId: String): DriveBotFiles {
        return driveBotService.getFileFromDb(fileId)
    }

    @PostMapping("/send-file")
    fun sendFile(@RequestParam("file") files: List<MultipartFile>) {
        driveBotService.sendFile(files)
    }

    @PostMapping("/download-file")
    fun downloadFile(@RequestBody filesIds: List<String>): ResponseEntity<ByteArray> {
        val files = filesIds.map { driveBotService.getFileFromDb(it) }
        val urls = driveBotService.downloadFiles(filesIds)

        val connection = URI(urls.first()).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream.readBytes()

            // @TODO add downloading for list of files

            val headers = HttpHeaders().apply {
                add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${files[0].fileName}")
                contentType = files[0].fileType?.let { MediaType.parseMediaType(it) }
            }

            return ResponseEntity.ok().headers(headers).body(inputStream)
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    fun deleteFile(@PathVariable fileId: String) {
        // @TODO delete file from db and from drive
    }
}