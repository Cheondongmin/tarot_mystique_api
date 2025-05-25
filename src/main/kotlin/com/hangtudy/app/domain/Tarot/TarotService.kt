package com.hangtudy.domain.Tarot

import com.fasterxml.jackson.databind.ObjectMapper
import com.hangtudy.app.domain.Tarot.Activity
import com.hangtudy.app.domain.Tarot.ActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TarotService(
    private val activityRepository: ActivityRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(TarotService::class.java)

    fun addTarot(
        category: String,
        userIp: String,
        userContent: String,
        resultContent: String
    ) {
        runCatching {
            // JSON 파싱 시도
            val parsedResult = runCatching {
                objectMapper.readValue(resultContent, Activity.TarotResult::class.java)
            }.onFailure { e ->
                logger.error("JSON 파싱 실패: ${e.message}", e)
            }.getOrNull()

            // Activity 생성
            val activity = Activity.create(
                category = category,
                ipAddress = userIp,
                userContent = userContent,
                resultContent = resultContent,
                resultData = parsedResult
            )

            // 저장
            activityRepository.save(activity)
        }.onFailure { e ->
            logger.error("addTarot 전체 처리 실패: ${e.message}", e)
            throw e
        }
    }
}