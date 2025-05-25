package com.hangtudy.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class PerformanceConfig {

    @Bean(name = ["taskExecutor"])
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10        // 기본 스레드 수
        executor.maxPoolSize = 50         // 최대 스레드 수  
        executor.queueCapacity = 100      // 큐 용량
        executor.setThreadNamePrefix("Async-")
        executor.initialize()
        return executor
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            // JSON 파싱 성능 최적화
            findAndRegisterModules()
        }
    }
}