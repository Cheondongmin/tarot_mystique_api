package com.hangtudy.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "http://localhost:3000",          // 개발환경 React
                "http://localhost:8080",          // 개발환경 Spring Boot
                "http://182.209.102.132:8080",    // 운영서버
                "https://your-domain.com"         // 실제 도메인 (나중에 추가)
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600) // 1시간
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // 허용할 Origin 설정
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:*",
            "http://182.209.102.132:*",
            "https://*.your-domain.com"
        )
        
        // 허용할 HTTP 메서드
        configuration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        )
        
        // 허용할 헤더
        configuration.allowedHeaders = listOf("*")
        
        // 인증 정보 포함 허용
        configuration.allowCredentials = true
        
        // Preflight 요청 캐시 시간
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/api/**", configuration)
        
        return source
    }
}