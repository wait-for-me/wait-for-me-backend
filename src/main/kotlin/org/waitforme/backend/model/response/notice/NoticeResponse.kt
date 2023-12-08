package org.waitforme.backend.model.response.notice

import org.waitforme.backend.entity.notice.Notice
import java.time.LocalDateTime

data class NoticeResponse(
    val id: Int,
    val title: String,
    val contents: String,
    val createdDate: LocalDateTime
)

fun Notice.toNoticeResponse() = NoticeResponse(
    id = id,
    title = title,
    contents = contents,
    createdDate = getNoticeCreatedAt()
)