package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.model.request.admin.AdminAuthRequest
import org.waitforme.backend.model.response.admin.AdminAuthResponse
import org.waitforme.backend.service.AdminService

@RestController
@Tag(name = "어드민 API")
@RequestMapping("/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
class AdminController(
    private val adminService: AdminService,
) {

    @PreAuthorize("permitAll()") // 해당 메소드는 권한 체크 없이 접근 허용
    @PostMapping("/sign-up")
    fun signUpAdmin(
        @RequestBody request: AdminAuthRequest,
    ): AdminAuthResponse = adminService.signUp(request)

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    fun loginAdmin(
        @RequestBody request: AdminAuthRequest,
    ): AdminAuthResponse = adminService.login(request)

    // TODO : 관리자 승인 및 권한 설정해주는 API 만들기
}
