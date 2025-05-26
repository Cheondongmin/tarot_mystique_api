package com.hangtudy.app.interfaces.api.v1.tarot

import com.hangtudy.app.interfaces.api.v1.common.CommonRes
import com.hangtudy.app.interfaces.api.v1.tarot.req.AddTarotReq
import com.hangtudy.domain.Tarot.TarotService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tarot")
class TarotController(
    val tarotService: TarotService
) {    
    @PostMapping("/add")
    fun addTarot(
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String> {
        tarotService.addTarot(req.category, req.userIp, req.userContent, req.resultContent)
        return CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
    }

    @GetMapping("/message")
    fun addTarot(
        @RequestParam(defaultValue = "메시지") msg: String
    ): CommonRes<String> {
        tarotService.sendMessage(msg)
        return CommonRes.success("관리자에게 메시지가 성공적으로 전송 됐습니다.")
    }
}