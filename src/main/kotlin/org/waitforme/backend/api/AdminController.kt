package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.tags.Tag
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
class AdminController(
    private val adminService: AdminService,
) {
    @PostMapping("/sign-up")
    fun signUpAdmin(
        @RequestBody request: AdminAuthRequest,
    ): AdminAuthResponse = adminService.signUp(request)

    @PostMapping("/login")
    fun loginAdmin(
        @RequestBody request: AdminAuthRequest,
    ): AdminAuthResponse = adminService.login(request)
}
