package com.hangtudy.app.infrastructure.telegram

import com.google.gson.JsonObject
import com.hangtudy.app.domain.message.MessageSender
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.TimeUnit

@Component
class TelegramSenderImpl : MessageSender {

    private val logger = LoggerFactory.getLogger(TelegramSenderImpl::class.java)

    private var baseUrl: String = ""
    private var chatId: String = ""
    private var prefix: String = ""

    companion object {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .build()

        private val JSON = "application/json; charset=utf-8".toMediaType()
    }

    @Value("\${message.bot-token}")
    fun setTelegramBotToken(token: String) {
        baseUrl = "https://api.telegram.org/bot$token/sendMessage"
    }

    @Value("\${message.chat-id}")
    fun setChatId(id: String) {
        chatId = id
    }

    @Value("\${message.env}")
    fun setPrefix(value: String) {
        prefix = value
    }

    @Throws(IOException::class)
    override fun sendMessage(message: String) {
        logger.info("TelegramSender 에서 텔레그램 메시지 전송 msg:{}", message)

        val jsonObject = JsonObject().apply {
            addProperty("chat_id", chatId)
            addProperty("text", message)
            addProperty("parse_mode", "HTML")
        }

        val json = jsonObject.toString()
        val body = json.toRequestBody(JSON)

        val request = Request.Builder()
            .url(baseUrl)
            .post(body)
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body.string()
                    logger.error("Telegram API 에러. 상태 코드: {}, 에러 내용: {}", response.code, errorBody)
                } else {
                    logger.info("Telegram 메시지 전송 성공. 응답 코드: {}", response.code)
                }
            }
        } catch (e: Exception) {
            logger.error("Telegram API 호출 실패", e)
            throw e
        }
    }
}