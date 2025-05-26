package com.hangtudy.app.interfaces.api.v1.tarot

import com.fasterxml.jackson.databind.ObjectMapper
import com.hangtudy.app.interfaces.api.v1.common.CommonRes
import com.hangtudy.app.interfaces.api.v1.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.api.v1.tarot.req.TarotMessageReq
import com.hangtudy.domain.Tarot.TarotService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tarot")
class TarotController(
    private val tarotService: TarotService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(TarotController::class.java)
    
    @PostMapping("/add")
    fun addTarot(
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String> {
        // 요청 Body JSON 로그
        try {
            val requestJson = objectMapper.writeValueAsString(req)
            logger.info("REQUEST_BODY: {}", requestJson)
        } catch (e: Exception) {
            logger.error("요청 Body JSON 변환 실패", e)
        }
        
        tarotService.addTarot(req.category, req.userIp, req.userContent, req.resultContent)
        val response = CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
        
        // 응답 Body JSON 로그
        try {
            val responseJson = objectMapper.writeValueAsString(response)
            logger.info("RESPONSE_BODY: {}", responseJson)
        } catch (e: Exception) {
            logger.error("응답 Body JSON 변환 실패", e)
        }
        
        return response
    }

    @PostMapping("/message")
    fun sendMessage(
        @Valid @RequestBody req: TarotMessageReq,
    ): CommonRes<String> {
        // 요청 Body JSON 로그
        try {
            val requestJson = objectMapper.writeValueAsString(req)
            logger.info("REQUEST_BODY: {}", requestJson)
        } catch (e: Exception) {
            logger.error("요청 Body JSON 변환 실패", e)
        }
        
        tarotService.sendMessage(req.msg)
        val response = CommonRes.success("관리자에게 메시지가 성공적으로 전송 됐습니다.")
        
        // 응답 Body JSON 로그
        try {
            val responseJson = objectMapper.writeValueAsString(response)
            logger.info("RESPONSE_BODY: {}", responseJson)
        } catch (e: Exception) {
            logger.error("응답 Body JSON 변환 실패", e)
        }
        
        return response
    }
}