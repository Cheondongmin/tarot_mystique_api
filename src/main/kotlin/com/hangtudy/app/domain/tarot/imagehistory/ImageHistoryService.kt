package com.hangtudy.app.domain.tarot.imagehistory

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageHistoryService(
    private val imageHistoryRepository: ImageHistoryRepository
) {
    private val logger = LoggerFactory.getLogger(ImageHistoryService::class.java)

    @Transactional
    fun createImageHistory(
        activityId: String,
        actionType: ImageHistory.ImageActionType,
        deviceInfo: ImageHistory.DeviceInfo
    ): Result<ImageHistory> {
        return runCatching {
            logger.info("🖼️ Creating image history - Activity: $activityId, Action: ${actionType.description}")

            val imageHistory = ImageHistory.create(
                activityId = activityId,
                actionType = actionType,
                deviceInfo = deviceInfo
            )

            imageHistoryRepository.save(imageHistory)
        }.onFailure { exception ->
            logger.error("❌ Failed to create image history: ${exception.message}", exception)
            when (exception) {
                is RuntimeException -> throw exception
                else -> throw RuntimeException("Failed to create image history: ${exception.message}", exception)
            }
        }
    }
}