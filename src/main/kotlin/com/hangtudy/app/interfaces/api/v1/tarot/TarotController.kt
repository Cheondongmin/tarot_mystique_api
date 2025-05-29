package com.hangtudy.app.interfaces.api.v1.tarot

import com.hangtudy.app.interfaces.api.v1.common.CommonRes
import com.hangtudy.app.interfaces.api.v1.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.api.v1.tarot.req.TarotMessageReq
import com.hangtudy.app.interfaces.api.v1.tarot.res.GetTarotListRes
import com.hangtudy.app.domain.tarot.TarotService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tarot")
@Tag(name = "Tarot", description = "타로 관련 API")
class TarotController(
    private val tarotService: TarotService
) {
    
    @PostMapping("/add")
    @Operation(summary = "타로 데이터 추가", description = "새로운 타로 점술 결과를 저장합니다.")
    fun addTarot(
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String> {
        tarotService.addTarot(req.category, req.userIp, req.userContent, req.resultContent)
        return CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
    }

    @PostMapping("/message")
    @Operation(summary = "메시지 전송", description = "관리자에게 메시지를 전송합니다.")
    fun sendMessage(
        @Valid @RequestBody req: TarotMessageReq,
    ): CommonRes<String> {
        tarotService.sendMessage(req.msg, req.userIp)
        return CommonRes.success("관리자에게 메시지가 성공적으로 전송 됐습니다.")
    }
    
    @GetMapping("/list")
    @Operation(summary = "타로 목록 조회", description = "저장된 타로 데이터를 페이징하여 조회합니다.")
    fun getTarotList(
        @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "페이지 크기 (기본값: 10, 최대: 100)")
        @RequestParam(defaultValue = "10") limit: Int
    ): CommonRes<GetTarotListRes> {
        val pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pagedResult = tarotService.getTarotList(pageable)
        
        val response = GetTarotListRes(
            items = pagedResult.content.map { GetTarotListRes.TarotItemRes.from(it) },
            totalCount = pagedResult.totalElements,
            page = pagedResult.number,
            pageSize = pagedResult.size,
            totalPages = pagedResult.totalPages
        )
        
        return CommonRes.success(response)
    }
}
