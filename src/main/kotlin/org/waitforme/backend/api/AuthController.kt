package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.request.auth.*
import org.waitforme.backend.model.response.auth.AuthResponse
import org.waitforme.backend.model.response.auth.CheckNameResponse
import org.waitforme.backend.service.AuthService
import javax.validation.Valid

@RestController
@Tag(name = "회원가입/로그인 API")
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "로컬 회원가입 시 인증 요청", description = "로컬 회원가입 시 인증 요청")
    @PostMapping("/local")
    fun requestAuthLocal(
        @Valid @RequestBody request: LocalAuthRequest,
    ): Boolean {
        return authService.requestAuthLocal(request = request)
    }

    @Operation(summary = "로컬 회원가입 시 인증 확인", description = "로컬 회원가입 시 인증 확인")
    @PutMapping("/local")
    fun verifyAuthLocal(
        @Valid @RequestBody request: LocalAuthRequest,
    ): Boolean {
        return authService.verifyAuthLocal(request = request)
    }

    @Operation(summary = "로컬 회원가입", description = "로컬 회원가입을 합니다.")
    @PostMapping("/local/sign-up")
    fun signUpLocal(
        @Valid @RequestBody request: LocalSignUpRequest,
    ): AuthResponse {
        return authService.signUpLocal(request = request)
    }

    @Operation(summary = "로컬 로그인", description = "로컬 로그인을 합니다.")
    @PostMapping("/local/sign-in")
    fun signInLocal(
        @Valid @RequestBody request: LocalSignInRequest,
    ): AuthResponse {
        return authService.signInLocal(request = request)
    }

    @Operation(summary = "리프레시 토큰으로 액세스 토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.")
    @PostMapping("/local/refresh")
    fun refresh(@RequestBody request: LocalRefreshTokenRequest): JwtDto {
        return authService.refresh(request.refreshToken)
    }

    @Operation(summary = "SNS 회원가입", description = "SNS 회원가입을 합니다.")
    @PostMapping("/sns/sign-up")
    fun signUpSns(
        @Valid @RequestBody request: SnsSignUpRequest,
    ): AuthResponse {
        return authService.signUpSns(request = request)
    }

    @Operation(summary = "SNS 로그인", description = "SNS 로그인을 합니다.")
    @PostMapping("/sns/sign-in")
    fun signInSns(
        @Valid @RequestBody request: SnsSignInRequest,
    ): AuthResponse {
        return authService.signInSns(request = request)
    }

    @Operation(summary = "닉네임(LOCAL 이름) 중복 체크", description = "닉네임(LOCAL 이름)의 중복 여부를 체크합니다.")
    @PostMapping("/check/name")
    fun checkDuplicateName(
        request: CheckNameRequest,
    ): CheckNameResponse {
        return authService.checkDuplicateName(request = request)
    }
}
