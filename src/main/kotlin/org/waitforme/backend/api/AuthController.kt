package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.model.request.auth.LocalAuthRequest
import org.waitforme.backend.model.response.auth.AuthResponse
import org.waitforme.backend.service.AuthService

@RestController
@Tag(name = "회원가입/로그인 API")
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(summary = "로컬 회원가입", description = "로컬 회원가입을 합니다.")
    @PostMapping("/sign-up/local")
    fun signUpLocal(
        @RequestBody request: LocalAuthRequest,
    ): AuthResponse {
        return authService.signUpLocal(request = request)
    }

    @Operation(summary = "로컬 로그인", description = "로컬 로그인을 합니다.")
    @PostMapping("/sign-in/local")
    fun signInLocal(
        @RequestBody request: LocalAuthRequest,
    ): AuthResponse {
        return authService.signInLocal(request = request)
    }
}
