package com.hangtudy.domain.Tarot

import com.fasterxml.jackson.databind.ObjectMapper
import com.hangtudy.app.domain.message.MessageSender
import com.hangtudy.app.domain.tarot.Activity
import com.hangtudy.app.domain.tarot.ActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TarotService(
    private val activityRepository: ActivityRepository,
    private val messageSender: MessageSender,
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

    fun getRecentTarotCount(minutes: Int): Long {
        return runCatching {
            val count = activityRepository.countRecentActivities(minutes)

            // 0 이상이면 텔레그램 메시지 전송
            if (count > 0) {
                val message = "🔮 타로 활동 알림\n\n" +
                        "최근 ${minutes}분 동안 ${count}건의 타로 활동이 있었습니다.\n\n" +
                        "\uD83D\uDD70\uFE0F 도착 시간 \n" +
                        " ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}"

                try {
                    messageSender.sendMessage(message)
                    logger.info("타로 활동 알림 전송 완료 $count}건")
                } catch (e: Exception) {
                    logger.error("텔레그램 메시지 전송 실패: ${e.message}", e)
                }
            }

            count
        }.onFailure { e ->
            logger.error("getRecentTarotCount 처리 실패 : ${e.message}", e)
        }.getOrDefault(0L)
    }

    fun sendMessage(message: String) {
        runCatching {
            val formattedMessage =
                """
               ✨ 마법의 편지가 도착했습니다 ✨
               
               📜 편지 내용
               ${message}
               
               🕰️ 도착 시간
               ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
           """.trimIndent()

            messageSender.sendMessage(formattedMessage)
        }.onFailure { e ->
            logger.error("sendMessage 처리 실패 : ${e.message}", e)
        }
    }
}