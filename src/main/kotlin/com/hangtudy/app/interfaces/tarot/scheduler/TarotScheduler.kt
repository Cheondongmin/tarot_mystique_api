package com.hangtudy.app.interfaces.tarot.scheduler

import com.hangtudy.app.application.TarotFacade
import com.hangtudy.app.domain.tarot.activity.ActivityService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TarotScheduler(
    private val tarotFacade: TarotFacade
) {
    private val logger = LoggerFactory.getLogger(TarotScheduler::class.java)

    @Scheduled(cron = "0 0 * * * *") // 매시 정각 (00분 00초)
    fun logRecentTarotCount() {
        try {
            val count = tarotFacade.getRecentTarotCount(60) // 60분 = 1시간
            logger.info("최근 1시간 이내 등록된 타로 데이터: ${count}개")
        } catch (e: Exception) {
            logger.error("타로 데이터 개수 조회 중 오류 발생: ${e.message}", e)
        }
    }
}