package org.waitforme.backend.common.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.dto.TokenDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtTokenProvider {

    lateinit var tokenSecretKey: String
    lateinit var accessTokenValidTime: String
    lateinit var refreshTokenValidTime: String
    lateinit var key: SecretKey

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

    private fun toLocalDateTime(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}
