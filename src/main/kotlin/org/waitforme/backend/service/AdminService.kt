package org.waitforme.backend.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.waitforme.backend.config.security.JwtTokenProvider
import org.waitforme.backend.entity.admin.Admin
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
        val admin = adminRepository.findAdminByEmailAndIsDeleted(email = request.email, isDeleted = false)?.let {
            throw InvalidParameterException("이미 가입된 어드민입니다.") // TODO : 예외 세분화하기
        } ?: run {
            adminRepository.save(
                Admin(
                    email = request.email,
                    name = request.name,
                    password = passwordEncoder.encode(request.password),
                    authority = request.authority, // TODO : 권한은 어떻게 줄 것인지?
                ),
            )
        }
        return createToken(admin).toAdminAuthResponse(authority = admin.authority)
    }

    fun login(request: AdminAuthRequest): AdminAuthResponse {
        return adminRepository.findAdminByEmailAndIsDeleted(email = request.email, isDeleted = false)?.let { admin ->
            when (passwordEncoder.matches(admin.password, request.password)) {
                true -> createToken(admin).toAdminAuthResponse(authority = admin.authority)
                false -> throw InvalidParameterException("잘못된 비밀번호입니다.")
            }
        } ?: throw InvalidParameterException("유효하지 않은 관리자입니다.")
    }

    // TODO : refreshToken?

    private fun createToken(admin: Admin) =
        jwtTokenProvider.createJwt(id = admin.id, email = admin.email, name = admin.name)
}
