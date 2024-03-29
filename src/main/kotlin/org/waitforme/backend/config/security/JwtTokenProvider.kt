package org.waitforme.backend.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.waitforme.backend.common.Logger
import org.waitforme.backend.entity.user.UserRefreshToken
import org.waitforme.backend.enums.UserRole
import org.waitforme.backend.model.LoginAdmin
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.dto.TokenDto
import org.waitforme.backend.model.toLoginUser
import org.waitforme.backend.repository.admin.AdminRepository
import org.waitforme.backend.repository.user.UserRefreshTokenRepository
import org.waitforme.backend.repository.user.UserRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey
import javax.security.auth.message.AuthException
import javax.servlet.http.HttpServletRequest

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtTokenProvider(
    private val adminRepository: AdminRepository,
    private val userRepository: UserRepository,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
) {

    lateinit var tokenSecretKey: String
    lateinit var accessTokenValidTime: String
    lateinit var refreshTokenValidTime: String
    lateinit var key: SecretKey

    companion object : Logger() {
        private const val BEARER_TYPE = "Bearer "
        private const val AUTHORITIES_KEY = "authorities"
    }

    @PostConstruct
    fun init() {
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.toByteArray())

        val keyBytes = Decoders.BASE64.decode(tokenSecretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(id: Int, name: String, expireDate: Date?, role: UserRole): String {
        val grantedAuthorities = SimpleGrantedAuthority(role.name).authority

        return Jwts.builder()
            .setId(id.toString())
            .setSubject(name)
            .setExpiration(expireDate)
            .claim("authorities", grantedAuthorities)
            .signWith(key)
            .compact()
    }

    fun createJwt(id: Int, account: String, name: String, role: UserRole): JwtDto {
        val now = System.currentTimeMillis()
        val nowLocalDateTime = toLocalDateTime(Instant.ofEpochMilli(now))

        val accessTokenExpireTime = Date(now + accessTokenValidTime.toLong())
        val refreshTokenExpireTime = Date(now + refreshTokenValidTime.toLong())

        val accessToken = createToken(id, name, accessTokenExpireTime, role)
        val refreshToken = createToken(id, name, refreshTokenExpireTime, role)

        // refreshToken 있으면 업데이트하고, 없으면 save
        userRefreshTokenRepository.findByIdOrNull(id)?.updateRefreshToken(refreshToken) ?: run {
            userRefreshTokenRepository.save(
                UserRefreshToken(
                    userId = id,
                    refreshToken = refreshToken,
                ),
            )
        }

        return JwtDto(
            accessToken = TokenDto(
                token = accessToken,
                createdAt = nowLocalDateTime,
                expiredAt = toLocalDateTime(accessTokenExpireTime.toInstant()),
            ),
            refreshToken = TokenDto(
                token = refreshToken,
                createdAt = nowLocalDateTime,
                expiredAt = toLocalDateTime(refreshTokenExpireTime.toInstant()),

            ),
            account = account,
            name = name,
        )
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        // ROLE
        val id = claims.id.toInt()
        val role = claims[AUTHORITIES_KEY].toString()
        val authorities = UserRole.valueOf(role).authorities.map { SimpleGrantedAuthority(it) }

        val userDetails =
            when (role) {
                UserRole.ADMIN.name -> {
                    adminRepository.findByIdOrNull(id)?.let { admin ->
                        LoginAdmin(admin)
                    } ?: throw UsernameNotFoundException(id.toString())
                }

                UserRole.USER.name -> {
                    userRepository.findByIdOrNull(id)?.let { user ->
                        // TODO : 확인 필요 - isOwner 체크 필요?
                        // findByIdAndIsOwner(id, true) 으로 체크
                        user.toLoginUser()
                    } ?: throw UsernameNotFoundException(id.toString())
                }

                UserRole.OWNER.name -> {
                    userRepository.findByIdOrNull(id)?.let { user ->
                        // TODO : 확인 필요 - isOwner 체크 필요?
                        // findByIdAndIsOwner(id, true) 으로 체크
                        user.toLoginUser()
                    } ?: throw UsernameNotFoundException(id.toString())
                }

                else -> {
                    throw UsernameNotFoundException(id.toString())
                }
            }

        return UsernamePasswordAuthenticationToken(userDetails, token, authorities)
    }

    // Request Header에서 토큰 정보를 꺼내오기 위한 메소드
    fun resolveToken(request: HttpServletRequest): String {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7)
        }

        return ""
    }

    fun validateAccessToken(token: String): Boolean {
        return runCatching {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        }.onFailure {
            logger.warn("accessToken 만료 또는 잘못된 형식입니다.")
        }.getOrElse { false }
    }

    fun validateRefreshToken(token: String) {
        runCatching {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        }.onFailure {
            throw AuthException("refreshToken 만료 또는 잘못된 형식으로 로그인이 필요합니다.")
        }
    }

    private fun toLocalDateTime(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}
