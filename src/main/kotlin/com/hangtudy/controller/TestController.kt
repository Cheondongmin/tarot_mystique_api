package com.hangtudy.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RestController
@RequestMapping("/api")
class TestController {

    @Value("\${app.security.api-secret-key:qkrgPflacjsEhdals04301222}")
    private lateinit var secretKey: String

    @Value("\${app.security.token-validity-minutes:5}")
    private val tokenValidityMinutes: Long = 5

    // 공개 엔드포인트
    @GetMapping("/public/health")
    fun publicHealth(): Map<String, Any> {
        return mapOf(
            "status" to "OK",
            "message" to "Public endpoint - no auth required",
            "timestamp" to System.currentTimeMillis(),
            "service" to "Hangtudy Tarot API"
        )
    }

    // 테스트용 토큰 생성 (공개 엔드포인트)
    @GetMapping("/test/generate-token")
    fun generateTestToken(): Map<String, Any> {
        val timestamp = System.currentTimeMillis()
        val data = "$secretKey:$timestamp"
        val signature = generateSignature(data)
        val token = "$timestamp:$signature"
        val encodedToken = Base64.getEncoder().encodeToString(token.toByteArray(StandardCharsets.UTF_8))
        
        return mapOf(
            "status" to "OK",
            "message" to "Test token generated successfully",
            "token" to encodedToken,
            "client_id" to "web-client",
            "expires_in_minutes" to tokenValidityMinutes,
            "generated_at" to timestamp,
            "usage_example" to mapOf(
                "header_name" to "X-API-Auth",
                "header_value" to encodedToken,
                "client_header" to "X-Client-ID",
                "client_value" to "web-client"
            )
        )
    }

    // 토큰 검증 테스트 (공개 엔드포인트)
    @PostMapping("/test/validate-token")
    fun validateTestToken(
        @RequestBody request: Map<String, Any>,
        httpRequest: HttpServletRequest
    ): Map<String, Any> {
        val authToken = httpRequest.getHeader("X-API-Auth")
        val clientId = httpRequest.getHeader("X-Client-ID")
        
        val validation = validateToken(authToken, clientId)
        val tokenInfo = if (authToken != null) extractTokenInfo(authToken) else null
        
        return mapOf(
            "status" to if (validation) "VALID" else "INVALID",
            "message" to if (validation) "Token is valid" else "Token validation failed",
            "token_provided" to (authToken != null),
            "client_id_provided" to (clientId != null),
            "client_id" to (clientId ?: "not provided"),
            "token_info" to (tokenInfo ?: "no token"),
            "request_data" to request,
            "timestamp" to System.currentTimeMillis()
        )
    }

    // 보안 엔드포인트 - 토큰 필요
    @GetMapping("/secure/data")
    fun secureData(): Map<String, Any> {
        return mapOf(
            "status" to "OK",
            "message" to "Secure endpoint - auth required",
            "data" to listOf(
                mapOf("id" to 1, "name" to "타로카드 리딩"),
                mapOf("id" to 2, "name" to "운세 보기"),
                mapOf("id" to 3, "name" to "점술 상담")
            ),
            "timestamp" to System.currentTimeMillis()
        )
    }

    @PostMapping("/secure/create")
    fun createData(@RequestBody data: Map<String, Any>): Map<String, Any> {
        return mapOf(
            "status" to "CREATED",
            "message" to "Data created successfully",
            "received_data" to data,
            "timestamp" to System.currentTimeMillis()
        )
    }

    // curl 명령어 예시 생성
    @GetMapping("/test/curl-examples")
    fun generateCurlExamples(): Map<String, Any> {
        val token = generateTestTokenString()
        val baseUrl = "http://localhost:8080/api"
        
        return mapOf(
            "status" to "OK",
            "message" to "Curl command examples",
            "generated_token" to token,
            "examples" to mapOf(
                "public_health" to "curl $baseUrl/public/health",
                "generate_token" to "curl $baseUrl/test/generate-token",
                "validate_token" to """curl -X POST $baseUrl/test/validate-token \
  -H "Content-Type: application/json" \
  -H "X-API-Auth: $token" \
  -H "X-Client-ID: web-client" \
  -d '{"test": "data"}'""",
                "secure_get" to """curl $baseUrl/secure/data \
  -H "X-API-Auth: $token" \
  -H "X-Client-ID: web-client"""",
                "secure_post" to """curl -X POST $baseUrl/secure/create \
  -H "Content-Type: application/json" \
  -H "X-API-Auth: $token" \
  -H "X-Client-ID: web-client" \
  -d '{"name": "테스트 데이터", "type": "타로"}'"""
            )
        )
    }

    // 헤더 검사 도구
    @GetMapping("/test/headers")
    fun inspectHeaders(httpRequest: HttpServletRequest): Map<String, Any> {
        val headers = mutableMapOf<String, String>()
        val headerNames = httpRequest.headerNames
        
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            headers[headerName] = httpRequest.getHeader(headerName)
        }
        
        return mapOf(
            "status" to "OK",
            "message" to "Request headers inspection",
            "all_headers" to headers,
            "auth_header" to (httpRequest.getHeader("X-API-Auth") ?: "not found"),
            "client_id_header" to (httpRequest.getHeader("X-Client-ID") ?: "not found"),
            "timestamp" to System.currentTimeMillis()
        )
    }

    // 토큰 생성 헬퍼 메서드
    private fun generateTestTokenString(): String {
        val timestamp = System.currentTimeMillis()
        val data = "$secretKey:$timestamp"
        val signature = generateSignature(data)
        val token = "$timestamp:$signature"
        return Base64.getEncoder().encodeToString(token.toByteArray(StandardCharsets.UTF_8))
    }

    // HMAC-SHA256 서명 생성
    private fun generateSignature(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(keySpec)
        val bytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // 토큰 검증
    private fun validateToken(token: String?, clientId: String?): Boolean {
        if (token.isNullOrBlank() || clientId != "web-client") {
            return false
        }

        return try {
            val decodedToken = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
            val parts = decodedToken.split(":")
            
            if (parts.size != 2) return false
            
            val timestamp = parts[0].toLongOrNull() ?: return false
            val signature = parts[1]
            
            // 시간 검증
            val now = System.currentTimeMillis()
            val age = now - timestamp
            val maxAge = tokenValidityMinutes * 60 * 1000
            
            if (age > maxAge || age < 0) return false
            
            // 서명 검증
            val expectedSignature = generateSignature("$secretKey:$timestamp")
            signature == expectedSignature
        } catch (e: Exception) {
            false
        }
    }

    // 토큰 정보 추출
    private fun extractTokenInfo(token: String): Map<String, Any> {
        return try {
            val decodedToken = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
            val parts = decodedToken.split(":")
            
            if (parts.size >= 2) {
                val timestamp = parts[0].toLongOrNull()
                val now = System.currentTimeMillis()
                mapOf(
                    "timestamp" to (timestamp ?: "invalid"),
                    "age_seconds" to if (timestamp != null) (now - timestamp) / 1000 else "unknown",
                    "signature_length" to parts[1].length,
                    "expires_in_seconds" to if (timestamp != null) {
                        val maxAge = tokenValidityMinutes * 60 * 1000
                        val remaining = maxAge - (now - timestamp)
                        maxOf(0, remaining / 1000)
                    } else "unknown"
                )
            } else {
                mapOf("error" to "Invalid token format")
            }
        } catch (e: Exception) {
            mapOf("error" to "Failed to decode token: ${e.message}")
        }
    }
}
