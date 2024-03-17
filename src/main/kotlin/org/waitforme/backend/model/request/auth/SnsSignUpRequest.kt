package org.waitforme.backend.model.request.auth

import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.GenderType
import org.waitforme.backend.enums.Provider
import java.time.LocalDate
import javax.validation.constraints.Pattern

data class SnsSignUpRequest(
    val provider: Provider,
    val snsId: String,
    @field:Pattern(regexp = "010[0-9]{3,4}[0-9]{4}")
    val phoneNumber: String,
    val name: String,
    val email: String? = null,
    val birthedAt: LocalDate? = null,
    val gender: GenderType? = null,
    val profileImage: String? = null,
)

fun SnsSignUpRequest.toUserEntity(isOwner: Boolean, isAuth: Boolean) = User(
    provider = provider,
    snsId = snsId,
    phoneNumber = phoneNumber,
    name = name,
    email = email,
    birthedAt = birthedAt,
    gender = gender,
    profileImage = profileImage,
    isOwner = isOwner,
    isAuth = isAuth
)
