package org.waitforme.backend.model.request.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.Provider
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class LocalSignUpRequest(
    @field:Pattern(regexp = "010[0-9]{3,4}[0-9]{4}")
    val phoneNumber: String,
    val name: String? = null,
    @NotBlank
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\$@\$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,}\$")
    val password: String,
    val isOwner: Boolean
)

fun LocalSignUpRequest.toUserEntity(encoder: PasswordEncoder, isAuth: Boolean, userName: String) = User(
    provider = Provider.LOCAL,
    phoneNumber = phoneNumber,
    password = encoder.encode(password),
    name = userName,
    isOwner = isOwner,
    isAuth = isAuth,
)