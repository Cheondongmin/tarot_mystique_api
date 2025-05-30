package com.hangtudy.app.interfaces.health

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class HealthCheckResponse(
    val status: String = "UP",
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)

@RestController
@RequestMapping(path = ["api/v1/health"])
@Tag(name = "Health", description = "시스템 상태 확인 API")
class HealthController {
    @Operation(summary = "시스템 상태 확인", description = "시스템의 현재 상태 정보를 반환합니다")
    @ApiResponse(
        responseCode = "200",
        description = "시스템이 정상 작동 중입니다",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = HealthCheckResponse::class)
        )]
    )
    @GetMapping("/check")
    fun checkHealth(): ResponseEntity<HealthCheckResponse> {
        return ResponseEntity.ok(HealthCheckResponse())
    }
}