package com.hangtudy.app.interfaces.tarot.controller
import com.hangtudy.app.interfaces.common.CommonRes
import com.hangtudy.app.interfaces.tarot.req.AddImageHistoryReq
import com.hangtudy.app.interfaces.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.tarot.req.SendMessageReq
import com.hangtudy.app.interfaces.tarot.res.GetTarotListRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Tarot", description = "타로 관련 API")
interface TarotControllerInterface {

    @PostMapping("/add")
    @Operation(
        summary = "타로 데이터 추가",
        description = "새로운 타로 점술 결과를 저장합니다."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "타로 데이터 저장 성공",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        )
    ])
    fun addTarot(
        @Parameter(description = "타로 데이터 추가 요청", required = true)
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String>

    @PostMapping("/message")
    @Operation(
        summary = "메시지 전송",
        description = "관리자에게 메시지를 전송합니다."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "메시지 전송 성공",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 메시지 데이터",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "429",
            description = "요청 한도 초과",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        )
    ])
    fun sendMessage(
        @Parameter(description = "메시지 전송 요청", required = true)
        @Valid @RequestBody req: SendMessageReq
    ): CommonRes<String>

    @GetMapping("/list")
    @Operation(
        summary = "타로 목록 조회",
        description = "저장된 타로 데이터를 페이징하여 조회합니다."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "타로 목록 조회 성공",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 페이징 파라미터",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        )
    ])
    fun getTarotList(
        @Parameter(
            description = "페이지 번호 (0부터 시작)",
            example = "0",
            schema = Schema(minimum = "0", defaultValue = "0")
        )
        @RequestParam(defaultValue = "0") page: Int,
        
        @Parameter(
            description = "페이지 크기 (최대 100)",
            example = "10",
            schema = Schema(minimum = "1", maximum = "100", defaultValue = "10")
        )
        @RequestParam(defaultValue = "10") limit: Int
    ): CommonRes<GetTarotListRes>

    @PostMapping("/image-history")
    @Operation(
        summary = "이미지 히스토리 추가",
        description = "이미지 저장 또는 공유 히스토리를 기록합니다."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "이미지 히스토리 저장 성공",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [Content(schema = Schema(implementation = CommonRes::class))]
        )
    ])
    fun addImageHistory(
        @Parameter(description = "이미지 히스토리 추가 요청", required = true)
        @Valid @RequestBody req: AddImageHistoryReq
    ): CommonRes<String>
}
