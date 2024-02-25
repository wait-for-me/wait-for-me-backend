package org.waitforme.backend.model.response.admin

import org.waitforme.backend.enums.AdminAuthority
import org.waitforme.backend.model.dto.JwtDto
import org.waitforme.backend.model.dto.TokenDto

data class AdminAuthResponse(
    val accessToken: TokenDto,
    val refreshToken: TokenDto,
    val email: String,
    val name: String,
    val authority: AdminAuthority,
)

fun JwtDto.toAdminAuthResponse(authority: AdminAuthority) = AdminAuthResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    email = email,
    name = name,
    authority = authority
)