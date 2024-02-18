package org.waitforme.backend.repository.bookmark

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.waitforme.backend.model.dto.bookmark.BookmarkResultDto

interface BookmarkCustomRepository {
    fun getBookmarkList(userId: Int, pageable: Pageable): Page<BookmarkResultDto>
}