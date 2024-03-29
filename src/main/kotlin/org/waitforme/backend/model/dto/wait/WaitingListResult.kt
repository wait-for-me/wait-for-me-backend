package org.waitforme.backend.model.dto.wait

import org.waitforme.backend.enums.EntryStatus
import java.time.LocalDateTime

data class WaitingListResult(
    val shopId: Int,
    val name: String,
    val status: EntryStatus,
    val createdAt: LocalDateTime,
    val orderNo: Int,
    val headCount: Int,
)
