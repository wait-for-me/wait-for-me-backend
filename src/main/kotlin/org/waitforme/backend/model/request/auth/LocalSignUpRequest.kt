package org.waitforme.backend.model.request.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.Provider
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class LocalSignUpRequest(
    @field:Pattern(regexp = "010[0-9]{3,4}[0-9]{4}")
    val phoneNumber: String,
    @NotBlank
    val name: String,
    @NotBlank
    @field:Size(min = 7, max = 25)
    val password: String,
)

fun LocalSignUpRequest.toUserEntity(encoder: PasswordEncoder, isOwner: Boolean, isAuth: Boolean) = User(
    provider = Provider.LOCAL,
    phoneNumber = phoneNumber,
    password = encoder.encode(password),
    name = name,
    isOwner = isOwner,
    isAuth = isAuth,
)
