package com.hangtudy.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val requestLoggingInterceptor: RequestLoggingInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestLoggingInterceptor)
            .addPathPatterns("/api/**") // API 경로만 로깅
            .excludePathPatterns(
                "/api/health/**",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ) // 제외할 경로들
    }
}