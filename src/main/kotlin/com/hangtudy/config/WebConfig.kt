package com.hangtudy.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val requestLoggingInterceptor: RequestLoggingInterceptor,
    private val ipWhitelistFilter: IpWhitelistFilter
) : WebMvcConfigurer {
    
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestLoggingInterceptor)
            .addPathPatterns("/api/**")  // API 경로에만 적용
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // 정적 리소스 핸들러 추가
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
            
        // 루트 경로에서 index.html 서빙
        registry.addResourceHandler("/")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
    }

    @Bean("customCharacterEncodingFilter")
    fun characterEncodingFilter(): FilterRegistrationBean<CharacterEncodingFilter> {
        val filter = CharacterEncodingFilter().apply {
            setEncoding("UTF-8")
            setForceEncoding(true)
        }
        return FilterRegistrationBean(filter).apply {
            order = Ordered.HIGHEST_PRECEDENCE
            addUrlPatterns("/*")
        }
    }
    
    @Bean("ipWhitelistFilterRegistration")
    fun ipWhitelistFilterRegistration(): FilterRegistrationBean<IpWhitelistFilter> {
        return FilterRegistrationBean(ipWhitelistFilter).apply {
            order = Ordered.HIGHEST_PRECEDENCE + 1  // 인코딩 필터 다음에 실행
            addUrlPatterns("/api/*")  // API 경로에만 적용
        }
    }
}
