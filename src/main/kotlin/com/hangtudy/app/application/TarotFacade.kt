package com.hangtudy.app.application

import com.hangtudy.app.application.dto.TarotListDto
import com.hangtudy.app.application.dto.TarotItemDto
import com.hangtudy.app.domain.tarot.activity.ActivityService
import com.hangtudy.app.domain.tarot.imagehistory.ImageHistory
import com.hangtudy.app.domain.tarot.imagehistory.ImageHistoryService
import com.hangtudy.app.domain.tarot.message.MessageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class TarotFacade(
    private val activityService: ActivityService,
    private val messageService: MessageService,
    private val imageHistoryService: ImageHistoryService
) {
    private val logger = LoggerFactory.getLogger(TarotFacade::class.java)

    fun addTarot(
        category: String,
        userIp: String,
        userContent: String,
        resultContent: String
    ) {
        activityService.addTarot(category, userIp, userContent, resultContent)
            .onSuccess {
                logger.info("✅ Tarot creation completed successfully")
            }
            .onFailure { e ->
                logger.error("❌ Tarot creation failed: ${e.message}", e)
                throw e
            }
    }

    fun getTarotList(page: Int, limit: Int): TarotListDto {
        return runCatching {
            val pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedResult = activityService.getTarotList(pageable)
            TarotListDto(
                items = pagedResult.content.map { TarotItemDto.from(it) },
                totalCount = pagedResult.totalElements,
                page = pagedResult.number,
                pageSize = pagedResult.size,
                totalPages = pagedResult.totalPages
            )
        }.onFailure { e ->
            logger.error("getTarotList 처리 실패: ${e.message}", e)
            throw e
        }.getOrThrow()
    }

    fun sendMessage(message: String, userIp: String) {
        runCatching {
            messageService.sendMessage(message, userIp)
        }.onFailure { e ->
            logger.error("sendMessage 처리 실패: ${e.message}", e)
            throw e
        }
    }

    fun getRecentTarotCount(minutes: Int): Long {
        return runCatching {
            val count = activityService.countRecentActivities(minutes)
            messageService.getRecentTarotCount(minutes, count)
        }.onFailure { e ->
            logger.error("getRecentTarotCount 처리 실패: ${e.message}", e)
            throw e
        }.getOrDefault(0L)
    }

    fun addImageHistory(
        activityId: String,
        imageActionType: String,
        userAgent: String,
        browserName: String?,
        osName: String?,
        deviceType: String,
        ipAddress: String
    ) {
        runCatching {
            logger.info("🖼️ Adding image history - Activity: $activityId, Action: $imageActionType")

            // imageActionType을 enum으로 변환
            val actionType = try {
                ImageHistory.ImageActionType.valueOf(imageActionType.uppercase())
            } catch (e: IllegalArgumentException) {
                logger.error("❌ Invalid image action type: $imageActionType")
                throw IllegalArgumentException("유효하지 않은 이미지 액션 타입입니다: $imageActionType")
            }

            // deviceType을 enum으로 변환
            val deviceTypeEnum = try {
                ImageHistory.DeviceInfo.DeviceType.valueOf(deviceType.uppercase())
            } catch (e: IllegalArgumentException) {
                logger.error("❌ Invalid device type: $deviceType")
                throw IllegalArgumentException("유효하지 않은 디바이스 타입입니다: $deviceType")
            }

            // DeviceInfo 생성
            val deviceInfo = ImageHistory.DeviceInfo(
                userAgent = userAgent,
                browserName = browserName,
                osName = osName,
                deviceType = deviceTypeEnum,
                ipAddress = ipAddress,
            )

            // ImageHistory 생성 및 저장
            val imageHistory = imageHistoryService.createImageHistory(
                activityId = activityId,
                actionType = actionType,
                deviceInfo = deviceInfo
            ).getOrThrow()

            // Activity 업데이트
            activityService.updateActivityForImageHistory(imageHistory)

            logger.info("✅ Image history and activity updated successfully")
        }.onFailure { e ->
            logger.error("❌ addImageHistory 처리 실패: ${e.message}", e)
            throw e
        }
    }

}
