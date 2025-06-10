package com.hangtudy.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
@WebFilter(urlPatterns = ["/api/*"])
class ApiAuthFilter(
    private val objectMapper: ObjectMapper
) : Filter {

    @Value("\${app.security.api-secret-key:qkrgPflacjsEhdals04301222}")
    private lateinit var secretKey: String

    @Value("\${app.security.token-validity-minutes:5}")
    private val tokenValidityMinutes: Long = 5

    companion object {
        private const val AUTH_HEADER = "X-API-Auth"
        private const val CLIENT_ID_HEADER = "X-Client-ID"
        private const val ALLOWED_CLIENT_ID = "web-client"
        private const val HMAC_SHA256 = "HmacSHA256"
        private val logger = LoggerFactory.getLogger(ApiAuthFilter::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        
        val requestURI = httpRequest.requestURI
        val method = httpRequest.method

        // CORS 설정
        httpResponse.setHeader("Access-Control-Allow-Origin", "*")
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, X-API-Auth, X-Client-ID")

        // OPTIONS 요청은 바로 통과
        if ("OPTIONS" == method) {
            httpResponse.status = HttpServletResponse.SC_OK
            return
        }

        logger.debug("Processing request: $method $requestURI")

        // 공개 엔드포인트 체크
        if (isPublicEndpoint(requestURI)) {
            logger.debug("Public endpoint, skipping auth: $requestURI")
            chain.doFilter(request, response)
            return
        }

        // 인증 헤더 확인
        val authToken = httpRequest.getHeader(AUTH_HEADER)
        val clientId = httpRequest.getHeader(CLIENT_ID_HEADER)

        if (authToken.isNullOrBlank() || clientId.isNullOrBlank()) {
            logger.warn("Missing auth headers for: $requestURI")
            sendErrorResponse(httpResponse, "Missing authentication headers")
            return
        }

        // 토큰 검증
        if (!validateToken(authToken, clientId)) {
            logger.warn("Invalid token for: $requestURI")
            sendErrorResponse(httpResponse, "Invalid authentication token")
            return
        }

        logger.debug("Auth successful for: $requestURI")
        chain.doFilter(request, response)
    }

    private fun isPublicEndpoint(uri: String): Boolean {
        val publicPaths = listOf(
            "/api/public/",
            "/api/test/",        // 테스트 엔드포인트 추가
            "/api/health/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/static/",
            "/favicon.ico",
            "/error",
            "/api/v1/"
        )
        return publicPaths.any { uri.startsWith(it) }
    }

    private fun validateToken(token: String, clientId: String): Boolean {
        try {
            // 클라이언트 ID 검증
            if (clientId != ALLOWED_CLIENT_ID) {
                logger.debug("Invalid client ID: $clientId")
                return false
            }

            // 토큰 디코딩
            val decodedToken = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
            val parts = decodedToken.split(":")
            
            if (parts.size != 2) {
                logger.debug("Invalid token format")
                return false
            }

            val timestamp = parts[0].toLongOrNull() ?: return false
            val signature = parts[1]

            // 시간 검증
            val now = System.currentTimeMillis()
            val age = now - timestamp
            val maxAge = tokenValidityMinutes * 60 * 1000

            if (age > maxAge || age < 0) {
                logger.debug("Token expired or invalid time")
                return false
            }

            // 서명 검증
            val expectedSignature = generateSignature("$secretKey:$timestamp")
            return signature == expectedSignature

        } catch (e: Exception) {
            logger.error("Token validation error: ${e.message}")
            return false
        }
    }

    private fun generateSignature(data: String): String {
        val mac = Mac.getInstance(HMAC_SHA256)
        val keySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), HMAC_SHA256)
        mac.init(keySpec)
        val bytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun sendErrorResponse(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json; charset=UTF-8"
        
        val errorMap = mapOf(
            "error" to "Unauthorized",
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        
        try {
            response.writer.write(objectMapper.writeValueAsString(errorMap))
        } catch (e: Exception) {
            logger.error("Failed to write error response", e)
        }
    }
}
