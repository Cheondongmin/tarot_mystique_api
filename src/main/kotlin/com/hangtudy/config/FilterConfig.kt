package com.hangtudy.config

import com.hangtudy.filter.ApiAuthFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

    @Bean
    fun apiAuthFilterRegistration(apiAuthFilter: ApiAuthFilter): FilterRegistrationBean<ApiAuthFilter> {
        val registration = FilterRegistrationBean<ApiAuthFilter>()
        registration.filter = apiAuthFilter
        registration.addUrlPatterns("/api/*")
        registration.order = 1
        registration.setName("apiAuthFilter")
        return registration
    }
}
