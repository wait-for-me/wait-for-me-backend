package org.waitforme.backend.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.waitforme.backend.common.Logger
import org.waitforme.backend.enums.UserRole
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
        private const val AUTHORITIES_KEY = "authorities"
    }

    @PostConstruct
    fun init() {
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.toByteArray())

        val keyBytes = Decoders.BASE64.decode(tokenSecretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(id: Int, name: String, expireDate: Date?, role: List<UserRole>): String {
        val grantedAuthorities = role.map { SimpleGrantedAuthority(it.name).authority }

        return Jwts.builder()
            .setId(id.toString())
            .setSubject(name)
            .setExpiration(expireDate)
            .claim("authorities", grantedAuthorities)
            .signWith(key)
            .compact()
    }

    fun createJwt(id: Int, email: String, name: String, role: List<UserRole>): JwtDto {
        val now = System.currentTimeMillis()
        val nowLocalDateTime = toLocalDateTime(Instant.ofEpochMilli(now))

        val accessTokenExpireTime = Date(now + accessTokenValidTime.toLong())
        val refreshTokenExpireTime = Date(now + refreshTokenValidTime.toLong())

        val accessToken = createToken(id, name, accessTokenExpireTime, role)
        val refreshToken = createToken(id, name, refreshTokenExpireTime, role)

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

        // ROLE
        val roles = claims[AUTHORITIES_KEY] as List<String>
        val authorities = roles.map { SimpleGrantedAuthority(it) }

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
            logger.warn("accessToken 만료 또는 잘못된 형식입니다.")
        }.getOrElse { false }
    }

    private fun toLocalDateTime(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}