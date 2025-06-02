package com.hangtudy.app.application

import com.hangtudy.app.application.dto.TarotListDto
import com.hangtudy.app.application.dto.TarotItemDto
import com.hangtudy.app.domain.tarot.activity.ActivityService
import com.hangtudy.app.domain.tarot.message.MessageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class TarotFacade(
    private val activityService: ActivityService,
    private val messageService: MessageService
) {
    private val logger = LoggerFactory.getLogger(TarotFacade::class.java)

    fun addTarot(
        category: String,
        userIp: String,
        userContent: String,
        resultContent: String
    ) {
        runCatching {
            activityService.addTarot(category, userIp, userContent, resultContent)
        }.onFailure { e ->
            logger.error("addTarot 처리 실패: ${e.message}", e)
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
}