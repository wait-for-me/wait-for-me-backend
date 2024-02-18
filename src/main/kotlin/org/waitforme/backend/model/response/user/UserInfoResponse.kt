package org.waitforme.backend.model.response.user

import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.GenderType
import org.waitforme.backend.enums.Provider
import java.time.LocalDate

data class UserInfoResponse(
    val provider: Provider,
    val phoneNumber: String,
    var email: String? = null,
    var name: String,
    var birthedAt: LocalDate? = null,
    var gender: GenderType? = null,
    var profileImage: String? = null,
    var isOwner: Boolean = false,
    var isAdult: Boolean = false,
)

fun User.toUserInfoResponse() = UserInfoResponse(
    provider = provider,
    phoneNumber = phoneNumber,
    email = email,
    name = name,
    birthedAt = birthedAt,
    gender = gender,
    profileImage = profileImage,
    isOwner = isOwner,
    isAdult = isAdult,
)