package org.waitforme.backend.model.dto.wait

data class WaitingListOwnerResult(
    val name: String,
    val userPhone: String?,
    val nonUserPhone: String?,
    val callCount: Int,
    val headCount: Int,
    val orderNo: Int
)
