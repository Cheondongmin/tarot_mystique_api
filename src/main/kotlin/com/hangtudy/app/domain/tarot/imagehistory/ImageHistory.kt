package com.hangtudy.app.domain.tarot.imagehistory

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document(collection = "ImageHistory")
data class ImageHistory(
    @Id
    val id: String? = null,

    @Field("activity_id")
    val activityId: ObjectId,

    @Field("action_type")
    val actionType: ImageActionType,

    @Field("device_info")
    val deviceInfo: DeviceInfo,

    @Field("created_at")
    val createdAt: LocalDateTime,

    @Field("created_at_kst")
    val createdAtKst: LocalDateTime
) {
    companion object {
        fun create(
            activityId: String,
            actionType: ImageActionType,
            deviceInfo: DeviceInfo
        ): ImageHistory {
            val nowUtc = LocalDateTime.now()
            val nowKst = nowUtc.plusHours(9)

            return ImageHistory(
                activityId = ObjectId(activityId.trim()),
                actionType = actionType,
                deviceInfo = deviceInfo,
                createdAt = nowUtc,
                createdAtKst = nowKst
            )
        }
    }

    enum class ImageActionType(val description: String) {
        SAVE("저장"),
        SHARE("공유")
    }

    data class DeviceInfo(
        @Field("user_agent")
        val userAgent: String,

        @Field("browser_name")
        val browserName: String? = null,

        @Field("os_name")
        val osName: String? = null,

        @Field("device_type")
        val deviceType: DeviceType,

        @Field("ip_address")
        val ipAddress: String
    ) {
        enum class DeviceType(val description: String) {
            DESKTOP("데스크톱"),
            MOBILE("모바일"),
            TABLET("태블릿"),
            UNKNOWN("알 수 없음")
        }
    }
}