package org.waitforme.backend.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.waitforme.backend.common.Logger
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.dto.TokenDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtTokenProvider {

    lateinit var tokenSecretKey: String
    lateinit var accessTokenValidTime: String
    lateinit var refreshTokenValidTime: String
    lateinit var key: SecretKey

    companion object : Logger() {
        private const val BEARER_TYPE = "Bearer "
        private const val AUTHORITIES_KEY = "auth"
    }

    @PostConstruct
    fun init() {
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.toByteArray())

        val keyBytes = Decoders.BASE64.decode(tokenSecretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(id: Int, expireDate: Date?): String {
        return Jwts.builder()
            .setSubject(id.toString())
            .setExpiration(expireDate)
            .signWith(key)
            .compact()
    }

    fun createJwt(id: Int, email: String, name: String): JwtDto {
        val now = System.currentTimeMillis()
        val nowLocalDateTime = toLocalDateTime(Instant.ofEpochMilli(now))

        val accessTokenExpireTime = Date(now + accessTokenValidTime.toLong())
        val refreshTokenExpireTime = Date(now + refreshTokenValidTime.toLong())

        val accessToken = createToken(id, accessTokenExpireTime)
        val refreshToken = createToken(id, refreshTokenExpireTime)

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
            email = email,
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

        // ROLD_ADMIN 또는 ROLE_USER
        val authorities: Collection<GrantedAuthority> =
            claims[AUTHORITIES_KEY].toString().split(",")
                .map { SimpleGrantedAuthority(it) }
                .toList()

        val userDetails = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(userDetails, token, authorities)

//        val claims = parseClaims(accessToken)
//        val id = claims[Claims.SUBJECT] as String
//        val userDetails = customUserDetailsService.loadUserByUsername(id)
//        return UsernamePasswordAuthenticationToken(userDetails, "", listOf(SimpleGrantedAuthority("ROLE_USER")))
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
            logger.warn("accessToken 만료 또는 잘못된 형식으로 로그인이 필요합니다.")
        }.getOrElse { false }
    }

    private fun toLocalDateTime(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}
