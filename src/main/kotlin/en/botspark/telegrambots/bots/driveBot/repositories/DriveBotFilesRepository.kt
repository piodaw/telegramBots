package en.botspark.telegrambots.bots.driveBot.repositories

import en.botspark.telegrambots.bots.driveBot.entities.DriveBotFiles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DriveBotFilesRepository: JpaRepository<DriveBotFiles, Long>{
    fun findByFileId(fileId: String): DriveBotFiles
}