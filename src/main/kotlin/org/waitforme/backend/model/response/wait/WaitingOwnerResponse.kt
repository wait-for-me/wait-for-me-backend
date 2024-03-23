package org.waitforme.backend.model.response.wait

import org.waitforme.backend.model.dto.wait.WaitingListOwnerResult

data class WaitingOwnerResponse(
    val name: String,
    val phoneNumber: String,
    val callCount: Int,
    val headCount: Int,
    val orderNo: Int
)

fun WaitingListOwnerResult.toResponse(): WaitingOwnerResponse {
    return WaitingOwnerResponse(
        name = userPhone?.let { name } ?: run { nonUserPhone!!.substring(nonUserPhone.length - 4) },
        phoneNumber = userPhone ?: nonUserPhone!!,
        callCount = callCount,
        headCount = headCount,
        orderNo = orderNo
    )
}
