package org.waitforme.backend.model.response.notice

import org.waitforme.backend.entity.notice.Notice
import java.time.LocalDateTime

data class NoticeListResponse(
    val id: Int,
    val title: String,
    val createdAt: LocalDateTime,
)

fun List<Notice>.toNoticeListResponse() =
    this.map {
        NoticeListResponse(
            id = it.id,
            title = it.title,
            createdAt = it.getNoticeCreatedAt(),
        )
    }
