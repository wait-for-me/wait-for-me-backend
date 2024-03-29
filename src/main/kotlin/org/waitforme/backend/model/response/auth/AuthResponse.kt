package org.waitforme.backend.model.response.auth

import org.waitforme.backend.entity.user.User
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.dto.TokenDto

data class AuthResponse(
    val phoneNumber: String,
    val name: String,
    val isOwner: Boolean,
    val accessToken: TokenDto,
    val refreshToken: TokenDto,
)

fun User.toAuthResponse(token: JwtDto) = AuthResponse(
    accessToken = token.accessToken,
    refreshToken = token.refreshToken,
    phoneNumber = phoneNumber,
    name = name,
    isOwner = isOwner,
)
