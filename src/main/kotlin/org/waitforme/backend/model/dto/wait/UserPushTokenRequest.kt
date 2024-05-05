package org.waitforme.backend.model.dto.wait

import org.waitforme.backend.entity.user.UserPush

data class UserPushTokenRequest(
    val userId: Int,
    val pushToken: String,
    val deviceId: String,
)

fun UserPushTokenRequest.toEntity(id: Int? = 0) = UserPush(
    id = id ?: 0,
    userId = userId,
    pushToken = pushToken,
    deviceId = deviceId,
)
