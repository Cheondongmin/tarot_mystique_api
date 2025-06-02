package com.hangtudy.app.application

import com.hangtudy.app.domain.tarot.activity.ActivityService
import com.hangtudy.app.domain.tarot.message.MessageService
import com.hangtudy.app.interfaces.tarot.res.GetTarotListRes
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class TarotFacade(
    private val activityService: ActivityService,
    private val messageService: MessageService
) {
    fun addTarot(
        category: String,
        userIp: String,
        userContent: String,
        resultContent: String
    ) {
        runCatching {
            activityService.addTarot(category, userIp, userContent, resultContent)
        }.onFailure { e ->
            throw e
        }
    }

    fun getTarotList(page: Int, limit: Int): GetTarotListRes {
        return runCatching {
            val pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedResult = activityService.getTarotList(pageable)
            GetTarotListRes(
                items = pagedResult.content.map { GetTarotListRes.TarotItemRes.from(it) },
                totalCount = pagedResult.totalElements,
                page = pagedResult.number,
                pageSize = pagedResult.size,
                totalPages = pagedResult.totalPages
            )
        }.onFailure { e ->
            throw e
        }.getOrThrow()
    }

    fun sendMessage(message: String, userIp: String) {
        runCatching {
            messageService.sendMessage(message, userIp)
        }.onFailure { e ->
            throw e
        }
    }

    fun getRecentTarotCount(minutes: Int): Long {
        return runCatching {
            val count = activityService.countRecentActivities(minutes)
            messageService.getRecentTarotCount(minutes, count)
        }.onFailure { e ->
            throw e
        }.getOrDefault(0L)
    }
}