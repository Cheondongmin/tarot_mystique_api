package com.hangtudy.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IpWhitelistFilter(
    private val securityProperties: SecurityProperties
) : Filter {
    
    private val logger = LoggerFactory.getLogger(IpWhitelistFilter::class.java)
    
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        
        // IP 화이트리스트가 비활성화된 경우 모든 요청 허용
        if (!securityProperties.enabled) {
            chain.doFilter(request, response)
            return
        }
        
        val clientIp = getClientIpAddress(httpRequest)
        val requestUri = httpRequest.requestURI
        
        // 제외 경로 확인
        if (isExcludedPath(requestUri)) {
            chain.doFilter(request, response)
            return
        }
        
        // IP 검증
        if (!isAllowedIp(clientIp)) {
            logger.warn("차단된 IP에서의 접근 시도 - IP: {}, URI: {}, User-Agent: {}", 
                clientIp, requestUri, httpRequest.getHeader("User-Agent"))
            
            httpResponse.status = HttpServletResponse.SC_FORBIDDEN
            httpResponse.contentType = "application/json;charset=UTF-8"
            httpResponse.writer.write("""
                {
                    "resultType": "FAIL",
                    "data": {},
                    "exception": {
                        "code": "ACCESS_DENIED",
                        "message": "접근이 거부되었습니다. 허용되지 않은 IP입니다.",
                        "status": "FORBIDDEN",
                        "timestamp": "${System.currentTimeMillis()}"
                    }
                }
            """.trimIndent())
            return
        }
        
        logger.debug("허용된 IP에서의 접근 - IP: {}, URI: {}", clientIp, requestUri)
        chain.doFilter(request, response)
    }
    
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        val xRealIp = request.getHeader("X-Real-IP")
        val xForwarded = request.getHeader("X-Forwarded")
        val forwarded = request.getHeader("Forwarded")
        
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",")[0].trim()
            !xRealIp.isNullOrBlank() -> xRealIp
            !xForwarded.isNullOrBlank() -> xForwarded
            !forwarded.isNullOrBlank() -> forwarded
            else -> request.remoteAddr ?: "unknown"
        }
    }
    
    private fun isAllowedIp(clientIp: String): Boolean {
        return securityProperties.allowedIps.contains(clientIp)
    }
    
    private fun isExcludedPath(requestUri: String): Boolean {
        return securityProperties.excludedPaths.any { excludedPath ->
            if (excludedPath.endsWith("/**")) {
                requestUri.startsWith(excludedPath.removeSuffix("/**"))
            } else {
                requestUri == excludedPath || requestUri.startsWith(excludedPath)
            }
        }
    }
}
