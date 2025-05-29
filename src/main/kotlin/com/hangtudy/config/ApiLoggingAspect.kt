package com.hangtudy.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ApiLoggingAspect(
    private val objectMapper: ObjectMapper
) {
    
    private val logger = LoggerFactory.getLogger(ApiLoggingAspect::class.java)
    
    @Around("@within(org.springframework.web.bind.annotation.RestController) && " +
            "!execution(* com.hangtudy.app.interfaces.api.v1.health..*(..))")
    fun logApiCalls(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val className = joinPoint.target.javaClass.simpleName
        
        val startTime = System.currentTimeMillis()
        
        try {
            // 요청 파라미터 로깅
            val args = joinPoint.args
            if (args.isNotEmpty()) {
                args.forEachIndexed { index, arg ->
                    if (arg !is HttpServletRequest) {
                        try {
                            val argJson = objectMapper.writeValueAsString(arg)
                            logger.info("[$className.$methodName] REQUEST_BODY[$index]: {}", argJson)
                        } catch (e: Exception) {
                            logger.warn("[$className.$methodName] REQUEST_BODY[$index] JSON 변환 실패: {}", e.message)
                        }
                    }
                }
            }
            
            // 메서드 실행
            val result = joinPoint.proceed()
            
            val executionTime = System.currentTimeMillis() - startTime
            
            // 응답 로깅 (이미 CommonRes이므로 그대로 로깅)
            try {
                val responseJson = objectMapper.writeValueAsString(result)
                logger.info("[$className.$methodName] RESPONSE_BODY: {}", responseJson)
            } catch (e: Exception) {
                logger.warn("[$className.$methodName] RESPONSE_BODY JSON 변환 실패: {}", e.message)
            }
            
            logger.info("[$className.$methodName] API 처리 완료 - 실행시간: {}ms", executionTime)
            
            // 컨트롤러에서 이미 CommonRes로 반환하므로 그대로 반환
            return result
            
        } catch (ex: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            logger.error("[$className.$methodName] API 처리 중 예외 발생 - 실행시간: {}ms, 예외: {}", 
                executionTime, ex.message, ex)
            throw ex
        }
    }
}
