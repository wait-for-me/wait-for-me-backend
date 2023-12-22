package org.waitforme.backend.model.dto

import java.time.LocalDateTime

data class JwtDto(
    val accessToken: TokenDto,
    val refreshToken: TokenDto,
    val email: String,
    val name: String,
)

data class TokenDto(
    val token: String,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
)