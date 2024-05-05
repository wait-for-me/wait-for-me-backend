package org.waitforme.backend.model.dto.wait

import org.waitforme.backend.entity.user.UserPush

data class UserPushTokenRequest(
    val phoneNumber: String,
    val pushToken: String,
    val deviceId: String,
)

fun UserPushTokenRequest.toEntity(id: Int? = 0, userId: Int? = 0) = UserPush(
    id = id ?: 0,
    userId = userId,
    phoneNumber = phoneNumber,
    pushToken = pushToken,
    deviceId = deviceId,
)
