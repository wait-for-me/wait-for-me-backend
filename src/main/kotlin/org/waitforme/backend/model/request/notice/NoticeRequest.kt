package org.waitforme.backend.model.request.notice

import org.waitforme.backend.entity.notice.Notice

data class NoticeRequest(
    val title: String,
    val contents: String,
)

fun NoticeRequest.toNoticeEntity(id: Int?) = Notice(
    id = id ?: 0,
    title = title,
    contents = contents,
)
