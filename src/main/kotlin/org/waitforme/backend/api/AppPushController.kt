package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.LoginUser
import org.waitforme.backend.model.dto.wait.UserPushTokenRequest
import org.waitforme.backend.service.UserPushService

@RestController
@Tag(name = "APP PUSH API")
@RequestMapping("/v1/app")
class AppPushController(
    private val userPushService: UserPushService,
) {

    @PostMapping("/push")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "회원의 push Token 등록", description = "회원의 Push Token을 등록합니다.")
    suspend fun registerUserPushToken(
        @Parameter(hidden = true)
        @AuthenticationPrincipal
        loginUser: LoginUser,
        @RequestBody request: UserPushTokenRequest,
    ): Unit = userPushService.registerUserPushToken(request)

    // test
    @PostMapping("/test/fcm")
    fun testFcm(
    ): Unit = userPushService.sendPushMessage(
        targetToken = "",
        title = "TEST입니다",
        body = "TEST BODY입니다"
    )
}
