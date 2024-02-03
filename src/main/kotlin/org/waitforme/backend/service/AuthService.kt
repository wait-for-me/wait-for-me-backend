package org.waitforme.backend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.waitforme.backend.config.security.JwtTokenProvider
import org.waitforme.backend.entity.user.UserAuth
import org.waitforme.backend.enums.Provider
import org.waitforme.backend.enums.UserRole
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.request.auth.LocalAuthRequest
import org.waitforme.backend.model.request.auth.LocalSignInRequest
import org.waitforme.backend.model.request.auth.LocalSignUpRequest
import org.waitforme.backend.model.request.auth.toUserEntity
import org.waitforme.backend.model.response.auth.AuthResponse
import org.waitforme.backend.model.response.auth.toUserResponse
import org.waitforme.backend.repository.user.UserAuthRepository
import org.waitforme.backend.repository.user.UserRefreshTokenRepository
import org.waitforme.backend.repository.user.UserRepository
import java.security.InvalidParameterException
import java.time.Duration
import java.time.LocalDateTime
import javax.security.auth.message.AuthException

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val encoder: PasswordEncoder,
) {

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    fun requestAuthLocal(request: LocalAuthRequest): Boolean {
        userRepository.findByProviderAndPhoneNumber(
            provider = Provider.LOCAL,
            phoneNumber = request.phoneNumber,
        )?.let {
            if (it.isDeleted || it.deletedAt != null) {
                throw IllegalArgumentException("탈퇴 처리된 계정은 재가입할 수 없습니다.")
            }

            throw IllegalArgumentException("이미 등록된 계정입니다. 로그인 해주세요.")
        }

        // 난수 생성하여 인증번호 저장
        val authText = (100000..999999).random().toString()

        // 이미 요청을 보낸적이 있는지 확인
        userAuthRepository.findByPhoneNumber(phoneNumber = request.phoneNumber)?.let { userAuth ->
            val userAuthAt = userAuth.authenticatedAt
            val userRequestedAt = userAuth.requestedAt

            // 이미 인증이 완료되었는지 확인 - 15분 내로 인증을 받은 회원이어야 함
            if (userAuthAt != null && Duration.between(LocalDateTime.now(), userAuthAt).toMinutes() < 15) {
                throw IllegalArgumentException("이미 인증이 완료된 회원입니다. 회원가입을 진행해주세요.")
            }

            if (Duration.between(LocalDateTime.now(), userRequestedAt).seconds <= 30) {
                throw IllegalArgumentException("인증 번호 재요청은 요청을 보낸 후 30초 후에 가능합니다.")
            }

            userAuth.authText = authText
            userAuth.requestedAt = LocalDateTime.now()
        } ?: userAuthRepository.save(
            UserAuth(
                phoneNumber = request.phoneNumber,
                authText = authText,
            ),
        )

        // TODO : 인증 번호 전송

        // 전송 완료되면 true, 아니면 false
        return true
    }

    fun verifyAuthLocal(request: LocalAuthRequest): Boolean {
        return userAuthRepository.findByPhoneNumber(phoneNumber = request.phoneNumber)?.let { userAuth ->
            userAuth.authenticatedAt?.let {
                if (Duration.between(LocalDateTime.now(), userAuth.authenticatedAt).toMinutes() < 15) {
                    throw IllegalArgumentException("이미 인증이 완료된 회원입니다. 회원가입을 진행해주세요.")
                } else {
                    throw IllegalArgumentException("인증 유효시간 15분을 초과했습니다. 다시 인증을 시도해주세요.")
                }
            } ?: run {
                if (Duration.between(userAuth.requestedAt, LocalDateTime.now()).toMinutes() > 2) {
                    throw InvalidParameterException("인증 허용 시간 2분을 초과하였습니다. 인증 번호를 다시 요청해주세요.")
                }
            }

            if (userAuth.authText == request.authText) {
                userAuth.authenticatedAt = LocalDateTime.now()
                userAuthRepository.save(userAuth)
                true
            } else {
                false
            }
        } ?: throw IllegalArgumentException("인증 정보를 찾을 수 없습니다. 인증을 다시 요청해주세요.")
    }

    fun signUpLocal(request: LocalSignUpRequest): AuthResponse {
        return userAuthRepository.findByPhoneNumber(phoneNumber = request.phoneNumber)?.let {
            // 이미 인증이 완료되었는지 확인 - 15분 내로 인증을 받은 회원이어야 함
            it.authenticatedAt?.let { userAuthAt ->
                if (Duration.between(LocalDateTime.now(), userAuthAt).toMinutes() < 15) {
                    // 인증 완료 시 회원가입 완료
                    val user = userRepository.save(request.toUserEntity(encoder, isOwner = false, isAuth = true))
                    val token = jwtTokenProvider.createJwt(
                        id = user.id,
                        account = user.phoneNumber,
                        name = user.name,
                        role = UserRole.USER,
                    )

                    user.toUserResponse(token)
                } else {
                    throw IllegalArgumentException("인증 유효시간 15분을 초과했습니다. 다시 인증을 시도해주세요.")
                }
            }
        } ?: throw IllegalArgumentException("인증 정보를 찾을 수 없습니다. 인증을 다시 요청해주세요.")
    }

    @Transactional
    fun signInLocal(request: LocalSignInRequest): AuthResponse {
        val user = userRepository.findByProviderAndPhoneNumber(
            provider = Provider.LOCAL,
            phoneNumber = request.phoneNumber,
        )?.let { user ->
            user.checkAuthUser()
            user.checkDeletedUser()
            user
        } ?: throw AuthException("존재하지 않는 회원입니다. 회원 가입을 진행해주세요.")

        val token = if (encoder.matches(request.password, user.password)) {
            jwtTokenProvider.createJwt(
                id = user.id,
                account = user.phoneNumber,
                name = user.name,
                role = UserRole.USER,
            )
        } else {
            throw AuthException("비밀번호가 일치하지 않습니다.")
        }

        return user.toUserResponse(token)
    }

    @Transactional
    fun refresh(refreshToken: String): JwtDto {
        jwtTokenProvider.validateRefreshToken(refreshToken)

        // refreshToken이 저장되어있는지 확인
        val userRefreshToken = userRefreshTokenRepository.findByRefreshToken(refreshToken) ?: throw AuthException(
            "존재하지 않는 refreshToken입니다.",
        )

        // 유저 찾기
        val user = userRepository.findByIdOrNull(id = userRefreshToken.userId) ?: throw AuthException("존재하지 않는 유저라서 token 갱신에 실패했습니다.")
        user.checkAuthUser()
        user.checkDeletedUser()

        // token 새로 발급받기
        val token = jwtTokenProvider.createJwt(
                id = user.id,
                account = user.phoneNumber,
                name = user.name,
                role = UserRole.USER,
            )
        userRefreshToken.updateRefreshToken(token.refreshToken.token) // 새로 발급받은 refreshToken을 저장

        return token
    }
}
