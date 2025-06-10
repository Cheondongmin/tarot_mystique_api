package com.hangtudy.app.interfaces.auth.controller

import com.hangtudy.app.interfaces.auth.req.LoginReq
import com.hangtudy.app.interfaces.auth.req.RegisterReq
import com.hangtudy.app.interfaces.auth.res.LoginRes
import com.hangtudy.app.interfaces.auth.res.UserRes
import com.hangtudy.app.interfaces.common.CommonRes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "인증", description = "회원가입, 로그인 API")
interface AuthControllerInterface {
    
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterReq): CommonRes<UserRes>
    
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginReq): CommonRes<LoginRes>

    @Operation(summary = "인증", description = "이메일을 인증합니다.")
    @GetMapping("/verify")
    fun verifyEmail(@RequestParam token: String, email: String): CommonRes<UserRes>
}
