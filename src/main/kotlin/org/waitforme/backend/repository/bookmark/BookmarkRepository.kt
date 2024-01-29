package org.waitforme.backend.repository.bookmark

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.bookmark.Bookmark
import org.waitforme.backend.model.dto.bookmark.BookmarkResultDto

@Repository
interface BookmarkRepository : CrudRepository<Bookmark, Int>, BookmarkCustomRepository {
}