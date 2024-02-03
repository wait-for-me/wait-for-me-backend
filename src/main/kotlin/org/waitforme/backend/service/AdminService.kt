package org.waitforme.backend.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.waitforme.backend.config.security.JwtTokenProvider
import org.waitforme.backend.entity.admin.Admin
import org.waitforme.backend.enums.AdminAuthority
import org.waitforme.backend.enums.UserRole
import org.waitforme.backend.model.request.admin.AdminAuthRequest
import org.waitforme.backend.model.response.admin.AdminAuthResponse
import org.waitforme.backend.model.response.admin.toAdminAuthResponse
import org.waitforme.backend.repository.admin.AdminRepository
import java.security.InvalidParameterException

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {

    fun signUp(request: AdminAuthRequest): AdminAuthResponse {
        val admin = adminRepository.findAdminByEmail(email = request.email)?.let { admin ->
            if (admin.isDeleted!!) {
                throw InvalidParameterException("탈퇴 처리된 어드민입니다.")
            } else {
                throw InvalidParameterException("이미 가입된 어드민입니다.")
            }
        } ?: run {
            adminRepository.save(
                Admin(
                    email = request.email,
                    name = request.name,
                    password = passwordEncoder.encode(request.password),
                    authority = AdminAuthority.LEVEL_0, // 기본 권한 레벨은 0, 추후 관리자에 의해 레벨 조정
                ),
            )
        }
        return createAdminToken(admin).toAdminAuthResponse(authority = admin.authority)
    }

    fun signIn(request: AdminAuthRequest): AdminAuthResponse {
        return adminRepository.findAdminByEmail(email = request.email)?.let { admin ->
            if (admin.isDeleted!!) {
                throw InvalidParameterException("탈퇴 처리된 어드민입니다.")
            } else {
                when (passwordEncoder.matches(request.password, admin.password)) {
                    true -> createAdminToken(admin).toAdminAuthResponse(authority = admin.authority)
                    false -> throw InvalidParameterException("잘못된 비밀번호입니다.")
                }
            }
        } ?: throw InvalidParameterException("유효하지 않은 관리자입니다.")
    }

    private fun createAdminToken(admin: Admin) =
        jwtTokenProvider.createJwt(
            id = admin.id,
            account = admin.email,
            name = admin.name ?: "",
            role = UserRole.ADMIN,
        )
}
