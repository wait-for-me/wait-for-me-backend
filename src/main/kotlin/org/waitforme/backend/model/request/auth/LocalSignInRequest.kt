package org.waitforme.backend.model.request.auth

data class LocalSignInRequest(
    val phoneNumber: String,
    val password: String,
)
