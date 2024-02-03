package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.request.auth.LocalAuthRequest
import org.waitforme.backend.model.request.auth.LocalSignInRequest
import org.waitforme.backend.model.request.auth.LocalSignUpRequest
import org.waitforme.backend.model.response.auth.AuthResponse
import org.waitforme.backend.service.AuthService

@RestController
@Tag(name = "회원가입/로그인 API")
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "로컬 회원가입 시 인증 요청", description = "로컬 회원가입 시 인증 요청")
    @PostMapping("/local")
    fun requestAuthLocal(
        @RequestBody request: LocalAuthRequest,
    ): Boolean {
        return authService.requestAuthLocal(request = request)
    }

    @Operation(summary = "로컬 회원가입 시 인증 확인", description = "로컬 회원가입 시 인증 확인")
    @PutMapping("/local")
    fun verifyAuthLocal(
        @RequestBody request: LocalAuthRequest,
    ): Boolean {
        return authService.verifyAuthLocal(request = request)
    }

    @Operation(summary = "로컬 회원가입", description = "로컬 회원가입을 합니다.")
    @PostMapping("/local/sign-up")
    fun signUpLocal(
        @RequestBody request: LocalSignUpRequest,
    ): AuthResponse {
        return authService.signUpLocal(request = request)
    }

    @Operation(summary = "로컬 로그인", description = "로컬 로그인을 합니다.")
    @PostMapping("/local/sign-in")
    fun signInLocal(
        @RequestBody request: LocalSignInRequest,
    ): AuthResponse {
        return authService.signInLocal(request = request)
    }
}
