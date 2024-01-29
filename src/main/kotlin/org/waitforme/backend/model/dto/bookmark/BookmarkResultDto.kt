package org.waitforme.backend.model.dto.bookmark

import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.enums.ShopStatus
import java.time.LocalDate
import java.time.LocalTime

data class BookmarkResultDto(
    val shopId: Int,
    val shopName: String,
    val startedAt: LocalDate,
    val endedAt: LocalDate,
    val openedAt: LocalTime,
    val closedAt: LocalTime,
//    val status: EntryStatus,
    val updatedAt: LocalDate,
)
