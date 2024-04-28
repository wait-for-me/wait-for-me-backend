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
    val updatedAt: LocalDateTime, // TODO : 상태 변경이 반영된 시각을 뭘로 보여줄 것인지? -> updatedAt
)

fun Waiting.toResponse() = WaitingStatusResponse(
    shopId = shopId,
    userId = userId,
    entryStatus = status,
    orderNo = orderNo,
    callCount = callCount,
    updatedAt = getWaitingUpdatedAt()
)
