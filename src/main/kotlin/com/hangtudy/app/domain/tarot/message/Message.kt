package com.hangtudy.app.domain.tarot.message

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "Message")
data class Message(
    @Id
    val id: String? = null,

    @Field("ip_address")
    val ipAddress: String,

    @Field("user_message")
    val userMessage: String,

    @Field("created_at")
    val createdAt: LocalDateTime,

    @Field("created_at_kst")
    val createdAtKst: LocalDateTime
) {
    companion object {
        fun create(
            ipAddress: String,
            userMessage: String
        ): Message {
            val nowUtc = LocalDateTime.now()
            val nowKst = nowUtc.plusHours(9)

            return Message(
                ipAddress = ipAddress,
                userMessage = userMessage,
                createdAt = nowUtc,
                createdAtKst = nowKst
            )
        }
    }
}
