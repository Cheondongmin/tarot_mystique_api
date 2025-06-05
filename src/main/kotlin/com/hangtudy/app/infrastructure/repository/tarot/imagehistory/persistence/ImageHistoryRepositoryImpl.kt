package com.hangtudy.app.infrastructure.repository.tarot.imagehistory.persistence

import com.hangtudy.app.domain.tarot.imagehistory.ImageHistory
import com.hangtudy.app.domain.tarot.imagehistory.ImageHistoryRepository
import com.hangtudy.app.infrastructure.repository.tarot.imagehistory.repository.ImageHistoryMongoRepository
import org.springframework.stereotype.Repository

@Repository
class ImageHistoryRepositoryImpl(
    private val imageHistoryMongoRepository: ImageHistoryMongoRepository
): ImageHistoryRepository {
    override fun save(imageHistory: ImageHistory): ImageHistory {
        return imageHistoryMongoRepository.save(imageHistory)
    }
}