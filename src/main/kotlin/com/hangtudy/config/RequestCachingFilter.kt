package com.hangtudy.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(1)
class RequestCachingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        // Request와 Response를 캐싱 가능한 래퍼로 감싸기
        val cachingRequest = if (httpRequest !is ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper(httpRequest)
        } else {
            httpRequest
        }

        val cachingResponse = if (httpResponse !is ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper(httpResponse)
        } else {
            httpResponse
        }

        try {
            chain.doFilter(cachingRequest, cachingResponse)
        } finally {
            // Response body를 클라이언트에게 복사
            if (true) {
                cachingResponse.copyBodyToResponse()
            }
        }
    }
}