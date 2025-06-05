package com.hangtudy.app.domain.tarot.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.hangtudy.app.domain.tarot.imagehistory.ImageHistory
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ActivityService::class.java)

    @Transactional
    fun addTarot(
        category: String,
        userIp: String,
        userContent: String,
        resultContent: String
    ): Result<Unit> = runCatching {
        // JSON 파싱 시도
        val parsedResult = runCatching {
            objectMapper.readValue(resultContent, Activity.TarotResult::class.java)
        }.onFailure { e ->
            logger.warn("⚠️ JSON parsing failed, proceeding with raw content: ${e.message}")
        }.getOrNull()

        // Activity 생성
        val activity = Activity.create(
            category = category,
            ipAddress = userIp,
            userContent = userContent,
            resultContent = resultContent,
            resultData = parsedResult
        )
        activityRepository.save(activity)
    }.onFailure { exception ->
        logger.error("Transaction failed and will be rolled back: ${exception.message}", exception)
        when (exception) {
            is RuntimeException -> throw exception
            else -> throw RuntimeException("Transaction failed: ${exception.message}", exception)
        }
    }

    fun getTarotList(pageable: Pageable): Page<Activity> {
        return runCatching {
            logger.debug("📋 Fetching tarot list with page: ${pageable.pageNumber}, size: ${pageable.pageSize}")
            activityRepository.findAll(pageable)
        }.onFailure { e ->
            logger.error("❌ Failed to fetch tarot list: ${e.message}", e)
            throw RuntimeException("Failed to fetch tarot list", e)
        }.getOrThrow()
    }

    fun countRecentActivities(minutes: Int): Long {
        return runCatching {
            logger.debug("📊 Counting recent activities for last $minutes minutes")
            activityRepository.countRecentActivities(minutes)
        }.onFailure { e ->
            logger.error("❌ Failed to count recent activities: ${e.message}", e)
            throw RuntimeException("Failed to count recent activities", e)
        }.getOrThrow()
    }

    @Transactional
    fun updateActivityForImageHistory(imageHistory: ImageHistory) {
        runCatching {
            logger.info("🔄 Updating activity ${imageHistory.activityId} with image history ${imageHistory.id}")

            val activity = activityRepository.findById(imageHistory.activityId)
                ?: throw IllegalArgumentException("Activity not found with ID: ${imageHistory.activityId}")

            val imageHistoryId = imageHistory.id
                ?: throw IllegalArgumentException("ImageHistory ID is null")

            val updatedActivity = activity.updateImageHistoryId(imageHistoryId)
            activityRepository.save(updatedActivity)

            logger.info("✅ Activity updated successfully with image history ID: $imageHistoryId")
        }.onFailure { exception ->
            logger.error("Failed to update activity with image history: ${exception.message}", exception)
            when (exception) {
                is RuntimeException -> throw exception
                else -> throw RuntimeException("Failed to update activity: ${exception.message}", exception)
            }
        }
    }
}
