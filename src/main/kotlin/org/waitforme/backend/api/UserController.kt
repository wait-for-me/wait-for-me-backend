package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.LoginUser
import org.waitforme.backend.model.request.user.UserInfoRequest
import org.waitforme.backend.model.response.user.UserInfoResponse
import org.waitforme.backend.service.UserService

@RestController
@Tag(name = "유저 관련 API")
@RequestMapping("/v1/user")
class UserController(
    private val userService: UserService,
) {

    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    @GetMapping("/info")
    fun getUserInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal loginUser: LoginUser,
    ): UserInfoResponse = userService.getUserInfo(userId = loginUser.id)

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PutMapping(
        "/info",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun saveUserInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal loginUser: LoginUser,
        @RequestBody userInfoRequest: UserInfoRequest,
    ): UserInfoResponse = userService.saveUserInfo(userId = loginUser.id, request = userInfoRequest)
}
