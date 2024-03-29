package org.waitforme.backend.model.response.wait

import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.model.dto.wait.WaitingListResult
import java.time.LocalDateTime

data class WaitingResponse(
    val shopId: Int,
    val shopName: String,
    val entryStatus: EntryStatus,
    val createdAt: LocalDateTime,
    val orderNo: Int,
    val headCount: Int
)

fun WaitingListResult.toResponse() = WaitingResponse(
    shopId = shopId,
    shopName = name,
    entryStatus = status,
    createdAt = createdAt,
    orderNo = orderNo,
    headCount = headCount
)