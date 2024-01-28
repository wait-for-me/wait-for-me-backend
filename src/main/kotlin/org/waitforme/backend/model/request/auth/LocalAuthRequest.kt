package org.waitforme.backend.model.request.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.Provider

data class LocalAuthRequest(
    val phoneNumber: String,
    val name: String, // nickname?
    val password: String,
    val isOwner: Boolean,
)

fun LocalAuthRequest.toUserEntity(encoder: PasswordEncoder, isOwner: Boolean) = User(
    provider = Provider.LOCAL,
    phoneNumber = phoneNumber,
    password = encoder.encode(password),
    name = name,
    isOwner = isOwner
)
