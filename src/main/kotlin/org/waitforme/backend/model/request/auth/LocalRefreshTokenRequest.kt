package org.waitforme.backend.model.request.auth

data class LocalRefreshTokenRequest(
    val refreshToken: String,
)
