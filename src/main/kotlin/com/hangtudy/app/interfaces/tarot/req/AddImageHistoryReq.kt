package com.hangtudy.app.interfaces.tarot.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "이미지 히스토리 추가 요청")
data class AddImageHistoryReq(
    @Schema(description = "액티비티 ID", example = "684143a940ed1602875fc2b5", required = true)
    @field:NotBlank(message = "액티비티 ID는 필수입니다")
    val activityId: String,

    @Schema(description = "액션 타입", example = "SAVE", allowableValues = ["SAVE", "SHARE"], required = true)
    @field:NotBlank(message = "액션 타입은 필수입니다")
    @field:Pattern(regexp = "^(SAVE|SHARE)$", message = "액션 타입은 SAVE 또는 SHARE만 가능합니다")
    val actionType: String,

    // DeviceInfo에 필요한 필드들
    @Schema(description = "User Agent", example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36", required = true)
    @field:NotBlank(message = "User Agent는 필수입니다")
    val userAgent: String,

    @Schema(description = "브라우저명", example = "Chrome", required = false)
    val browserName: String? = null,

    @Schema(description = "운영체제명", example = "macOS", required = false)
    val osName: String? = null,

    @Schema(description = "디바이스 타입", example = "DESKTOP", allowableValues = ["DESKTOP", "MOBILE", "TABLET", "UNKNOWN"], required = true)
    @field:NotBlank(message = "디바이스 타입은 필수입니다")
    @field:Pattern(regexp = "^(DESKTOP|MOBILE|TABLET|UNKNOWN)$", message = "디바이스 타입은 DESKTOP, MOBILE, TABLET, UNKNOWN 중 하나여야 합니다")
    val deviceType: String,

    @Schema(description = "IP 주소", example = "192.168.1.100", required = true)
    @field:NotBlank(message = "IP 주소는 필수입니다")
    val ipAddress: String,
)