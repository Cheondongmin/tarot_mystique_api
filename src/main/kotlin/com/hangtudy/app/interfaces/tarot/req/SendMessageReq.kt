package com.hangtudy.app.interfaces.tarot.req

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "관리자 메시지 전송")
data class SendMessageReq(
    @field:NotBlank(message = "최대 5000자 입니다.")
    @field:Size(max = 5000, message = "내용은 5000자 이하로 입력해주세요.")
    @Schema(description = "내용", example = "좋은 만남이 기다리고 있습니다.")
    val msg: String,

    @field:NotBlank(message = "IP 주소는 필수입니다.")
    @field:Size(max = 60, message = "ip 60자 이하로 입력해주세요.")
    @Schema(description = "유저의 아이피", example = "127.0.0.1")
    val userIp: String
)