package org.waitforme.backend.model.response.wait

import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.enums.EntryStatus
import java.time.LocalDateTime

data class WaitingStatusResponse(
    val shopId: Int,
    val userId: Int,
    val entryStatus: EntryStatus,
    val orderNo: Int,
    val callCount: Int,
    val updatedAt: LocalDateTime, // 상태 변경이 반영된 시각은 updatedAt으로 참고
)

fun Waiting.toResponse() = WaitingStatusResponse(
    shopId = shopId,
    userId = userId ?: 0,
    entryStatus = status,
    orderNo = orderNo,
    callCount = callCount,
    updatedAt = getWaitingUpdatedAt(),
)
