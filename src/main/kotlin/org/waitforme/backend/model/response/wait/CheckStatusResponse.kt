package org.waitforme.backend.model.response.wait

import org.waitforme.backend.model.dto.wait.WaitingMembersResult

data class CheckStatusResponse(
    val orderNo: Int,
    val remainCount: Int,
)

fun WaitingMembersResult.toResponse() =
    CheckStatusResponse(
        orderNo = orderNo,
        remainCount = remainCount
    )