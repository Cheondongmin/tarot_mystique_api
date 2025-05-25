package com.hangtudy.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class RequestLoggingInterceptor(
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // 요청 시작 시간 저장
        request.setAttribute("startTime", System.currentTimeMillis())
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        try {
            val startTime = request.getAttribute("startTime") as? Long ?: System.currentTimeMillis()
            val duration = System.currentTimeMillis() - startTime

            val logData = createLogData(request, response, duration, ex)
            val jsonLog = objectMapper.writeValueAsString(logData)
            
            logger.info("API_REQUEST: $jsonLog")
        } catch (e: Exception) {
            logger.error("Failed to log request", e)
        }
    }

    private fun createLogData(
        request: HttpServletRequest,
        response: HttpServletResponse,
        duration: Long,
        exception: Exception?
    ): Map<String, Any?> {
        val requestBody = getRequestBody(request)
        val responseBody = getResponseBody(response)
        
        return mapOf(
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "method" to request.method,
            "uri" to request.requestURI,
            "queryString" to request.queryString,
            "headers" to getHeaders(request),
            "clientIp" to getClientIp(request),
            "requestBody" to requestBody,
            "responseStatus" to response.status,
            "responseBody" to responseBody,
            "duration" to "${duration}ms",
            "userAgent" to request.getHeader("User-Agent"),
            "referer" to request.getHeader("Referer"),
            "contentType" to request.contentType,
            "contentLength" to request.contentLength,
            "exception" to exception?.message
        )
    }

    private fun getRequestBody(request: HttpServletRequest): String? {
        return try {
            if (request is ContentCachingRequestWrapper) {
                val content = request.contentAsByteArray
                if (content.isNotEmpty()) {
                    String(content, Charsets.UTF_8)
                } else null
            } else null
        } catch (e: Exception) {
            "Failed to read request body: ${e.message}"
        }
    }

    private fun getResponseBody(response: HttpServletResponse): String? {
        return try {
            if (response is ContentCachingResponseWrapper) {
                val content = response.contentAsByteArray
                if (content.isNotEmpty()) {
                    String(content, Charsets.UTF_8)
                } else null
            } else null
        } catch (e: Exception) {
            "Failed to read response body: ${e.message}"
        }
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        request.headerNames?.let { headerNames ->
            while (headerNames.hasMoreElements()) {
                val name = headerNames.nextElement()
                val value = request.getHeader(name)
                headers[name] = value
            }
        }
        return headers
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val headers = listOf(
            "CF-Connecting-IP",
            "X-Original-Forwarded-For",
            "X-Forwarded-For",
            "X-Real-IP"
        )
        
        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrEmpty() && !ip.equals("unknown", ignoreCase = true)) {
                return ip.split(",")[0].trim()
            }
        }
        
        return request.remoteAddr ?: "unknown"
    }
}