package org.waitforme.backend.model.response.user

import org.waitforme.backend.entity.user.User
import org.waitforme.backend.enums.GenderType
import org.waitforme.backend.enums.Provider
import java.time.LocalDate

data class UserInfoResponse(
    val id: Int = 0,
    val provider: Provider,
    val phoneNumber: String,
    var email: String? = null,
    var name: String,
    var birthedAt: LocalDate? = null,
    var gender: GenderType? = null,
    var profileImage: String? = null,
    var isOwner: Boolean = false,
    var isAuth: Boolean = false, // 인증 여부, sns로 등록 시 자동 인증, local은 회원 가입 시 인증 절차 필요
    var isAdult: Boolean = false,
)

fun User.toUserInfoResponse() = UserInfoResponse(
    id = id,
    provider = provider,
    phoneNumber = phoneNumber,
    email = email,
    name = name,
    birthedAt = birthedAt,
    gender = gender,
    profileImage = profileImage,
    isOwner = isOwner,
    isAuth = isAuth,
    isAdult = isAdult,
)