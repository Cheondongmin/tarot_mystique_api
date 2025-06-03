package com.hangtudy.app.domain.tarot.message

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class MessageService(
    private val messageSender: MessageSender,
    private val messageRepository: MessageRepository
) {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    @Transactional
    fun sendMessage(message: String, userIp: String) {
        runCatching {
            val formattedMessage =
                """
               ✨ 마법의 편지가 도착했습니다 ✨
               
               📜 편지 내용
               ${message}
               
               👣 발자국
               ${userIp}
               
               🕰️ 도착 시간
               ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
           """.trimIndent()

            messageSender.sendMessage(formattedMessage)

            messageRepository.save(
                Message.create(
                    ipAddress = userIp,
                    userMessage = message
                )
            )
        }.onFailure { e ->
            logger.error("sendMessage 처리 실패 : ${e.message}", e)
            throw RuntimeException("Transaction failed: ${e.message}", e)
        }.getOrThrow()
    }

    @Transactional
    fun getRecentTarotCount(minutes: Int, count: Long): Long {
        return runCatching {
            // 0 이상이면 텔레그램 메시지 전송
            if (count > 0) {
                val message = "🔮 별들의 속삭임이 감지되었습니다.\n\n" +
                        "별빛 아래, ${minutes}번의 숨결 동안\n" +
                        "${count}건의 의식이 거행되었습니다."
                try {
                    messageSender.sendMessage(message)
                } catch (e: Exception) {
                    logger.error("텔레그램 메시지 전송 실패: ${e.message}", e)
                    throw RuntimeException("Transaction failed: ${e.message}", e)
                }
            }
            count
        }.onFailure { e ->
            logger.error("getRecentTarotCount 처리 실패 : ${e.message}", e)
            throw RuntimeException("Transaction failed: ${e.message}", e)
        }.getOrThrow()
    }
}