package com.hangtudy.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*

@Component
class RequestLoggingInterceptor : HandlerInterceptor {
    
    private val logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)
    private val securityLogger = LoggerFactory.getLogger("SECURITY")
    
    // 의심스러운 경로 패턴들
    private val suspiciousPatterns = listOf(
        ".php", ".asp", ".aspx", ".jsp", ".cgi",
        "wp-admin", "wp-login", "wordpress", "phpmyadmin",
        "admin", "login", "config", "backup", "dump", "shell", "cmd"
    )
    
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // 트랜잭션 ID 생성
        val transactionId = UUID.randomUUID().toString().substring(0, 8)
        val clientIp = getClientIpAddress(request)
        val userAgent = request.getHeader("User-Agent") ?: "Unknown"
        
        // MDC에 정보 설정
        MDC.put("X-HIT-TRANSACTION-ID", transactionId)
        MDC.put("X-HTTP-METHOD", request.method)
        MDC.put("X-HTTP-URL", request.requestURI)
        MDC.put("X-CLIENT-IP", clientIp)
        
        // 의심스러운 요청 체크
        if (isSuspiciousRequest(request.requestURI, userAgent)) {
            securityLogger.warn(
                "SUSPICIOUS_REQUEST - IP: {}, Method: {}, URI: {}, User-Agent: {}, Referer: {}",
                clientIp, request.method, request.requestURI, userAgent, 
                request.getHeader("Referer") ?: "Direct"
            )
        }
        
        // API 요청 로그
        logger.info("API 요청 시작 - {} {}", request.method, request.requestURI)
        
        // Query Parameter 로그
        if (request.queryString != null) {
            logger.info("Query Parameters: {}", request.queryString)
        }
        
        return true
    }
    
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        // POST/PUT/PATCH 요청의 경우 Body 정보 로그 (간접적으로)
        if (request.method in listOf("POST", "PUT", "PATCH")) {
            logger.info("요청 Content-Type: {}, Content-Length: {}", 
                request.contentType ?: "unknown", 
                request.contentLength)
        }
    }
    
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        // API 응답 로그
        logger.info("API 응답 완료 - Status: {}", response.status)
        
        if (ex != null) {
            logger.error("API 처리 중 예외 발생: {}", ex.message, ex)
        }
        
        // MDC 클리어
        MDC.clear()
    }
    
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        val xRealIp = request.getHeader("X-Real-IP")
        val xForwarded = request.getHeader("X-Forwarded")
        val forwarded = request.getHeader("Forwarded")
        val cfConnectingIp = request.getHeader("CF-Connecting-IP")
        
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",")[0].trim()
            !xRealIp.isNullOrBlank() -> xRealIp
            !cfConnectingIp.isNullOrBlank() -> cfConnectingIp
            !xForwarded.isNullOrBlank() -> xForwarded
            !forwarded.isNullOrBlank() -> forwarded
            else -> request.remoteAddr ?: "unknown"
        }
    }
    
    private fun isSuspiciousRequest(uri: String, userAgent: String): Boolean {
        val lowerUri = uri.lowercase()
        val lowerUserAgent = userAgent.lowercase()
        
        return suspiciousPatterns.any { pattern -> lowerUri.contains(pattern) } ||
               lowerUserAgent.contains("scanner") ||
               lowerUserAgent.contains("curl") ||
               lowerUserAgent.contains("wget") ||
               userAgent.length < 10
    }
}
