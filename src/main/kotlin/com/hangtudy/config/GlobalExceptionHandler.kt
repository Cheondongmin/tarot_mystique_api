package com.hangtudy.config

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class GlobalExceptionHandler : ErrorController {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    private val securityLogger = LoggerFactory.getLogger("SECURITY")
    
    @RequestMapping("/error")
    @ResponseBody
    fun handleError(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val status = request.getAttribute("jakarta.servlet.error.status_code") as? Int ?: 500
        val uri = request.getAttribute("jakarta.servlet.error.request_uri") as? String ?: request.requestURI
        val clientIp = getClientIpAddress(request)
        val userAgent = request.getHeader("User-Agent") ?: "Unknown"
        
        when (status) {
            404 -> {
                // 의심스러운 404 경로 체크
                if (isSuspiciousPath(uri)) {
                    securityLogger.warn(
                        "SUSPICIOUS_404 - IP: {}, URI: {}, User-Agent: {}",
                        clientIp, uri, userAgent
                    )
                }
                
                logger.warn(
                    "404 NOT FOUND - IP: {}, URI: {}, User-Agent: {}",
                    clientIp, uri, userAgent
                )
            }
            500 -> {
                logger.error(
                    "500 INTERNAL ERROR - IP: {}, URI: {}, User-Agent: {}",
                    clientIp, uri, userAgent
                )
            }
        }
        
        return ResponseEntity.status(status).body(mapOf(
            "error" to "Error $status",
            "path" to uri,
            "timestamp" to System.currentTimeMillis()
        ))
    }
    
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        val xRealIp = request.getHeader("X-Real-IP")
        val cfConnectingIp = request.getHeader("CF-Connecting-IP")
        
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",")[0].trim()
            !xRealIp.isNullOrBlank() -> xRealIp
            !cfConnectingIp.isNullOrBlank() -> cfConnectingIp
            else -> request.remoteAddr ?: "unknown"
        }
    }
    
    private fun isSuspiciousPath(path: String): Boolean {
        val lowerPath = path.lowercase()
        val suspiciousPatterns = listOf(
            ".php", ".asp", ".aspx", ".jsp", ".cgi",
            "wp-admin", "wp-login", "wordpress", "phpmyadmin",
            "admin", "config", "backup", "dump", "shell", "cmd"
        )
        
        return suspiciousPatterns.any { pattern -> lowerPath.contains(pattern) }
    }
}
