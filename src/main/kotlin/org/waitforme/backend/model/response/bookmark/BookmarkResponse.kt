package org.waitforme.backend.model.response.bookmark

import org.waitforme.backend.enums.ShopStatus
import org.waitforme.backend.model.dto.bookmark.BookmarkResultDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class BookmarkResponse(
    val shopId: Int,
    val shopName: String,
    val shopStatus: ShopStatus,
    val createdAt: LocalDate
)

fun BookmarkResultDto.toResponse(): BookmarkResponse {
    val nowDateTime = LocalDateTime.now()
    val shopOpenTime = LocalDateTime.of(startedAt, openedAt)
    val shopCloseTime = LocalDateTime.of(endedAt, closedAt)
    return BookmarkResponse(
        shopId = shopId,
        shopName = shopName,
        shopStatus = if (shopOpenTime > nowDateTime) ShopStatus.WAIT
            else if (shopOpenTime <= nowDateTime && nowDateTime < shopCloseTime) ShopStatus.PROGRESS
            else ShopStatus.CLOSED,
        createdAt = updatedAt
    )
}
