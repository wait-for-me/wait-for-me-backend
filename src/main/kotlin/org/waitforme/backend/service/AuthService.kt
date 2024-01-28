package org.waitforme.backend.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.waitforme.backend.config.security.JwtTokenProvider
import org.waitforme.backend.enums.Provider
import org.waitforme.backend.enums.UserRole
import org.waitforme.backend.model.request.auth.LocalAuthRequest
import org.waitforme.backend.model.request.auth.toUserEntity
import org.waitforme.backend.model.response.auth.AuthResponse
import org.waitforme.backend.model.response.auth.toUserResponse
import org.waitforme.backend.repository.user.UserRepository
import javax.security.auth.message.AuthException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val encoder: PasswordEncoder,
) {

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    fun signUpLocal(request: LocalAuthRequest): AuthResponse {
        userRepository.findByProviderAndPhoneNumber(
            provider = Provider.LOCAL,
            phoneNumber = request.phoneNumber,
        )?.let { user ->
            throw IllegalArgumentException("이미 등록된 계정입니다.")
        }

        val user = userRepository.save(request.toUserEntity(encoder, isOwner = false))
        val token = jwtTokenProvider.createJwt(id = user.id, account = user.phoneNumber, name = user.name, role = UserRole.USER)

        return user.toUserResponse(token)
    }

    @Transactional
    fun signInLocal(request: LocalAuthRequest): AuthResponse {
        val user = userRepository.findByProviderAndPhoneNumber(
            provider = Provider.LOCAL,
            phoneNumber = request.phoneNumber
        ) ?: throw AuthException("존재하지 않는 회원입니다. 회원 가입을 진행해주세요.")

        val token = if (encoder.matches(request.password, user.password)) {
            user.checkDeletedUser() // 탈퇴 회원인지 체크
            jwtTokenProvider.createJwt(id = user.id, account = user.phoneNumber, name = user.name, role = UserRole.USER)
        } else {
            throw AuthException("비밀번호가 일치하지 않습니다.")
        }

        return user.toUserResponse(token)
    }
}
