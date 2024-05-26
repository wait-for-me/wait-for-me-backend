package org.waitforme.backend.model.request.user

import org.waitforme.backend.entity.user.UserWithdraw

data class UserWithdrawRequest(
    val reason: String,
)

fun UserWithdrawRequest.toEntity(userId: Int) = UserWithdraw(
    userId = userId,
    reason = reason
)