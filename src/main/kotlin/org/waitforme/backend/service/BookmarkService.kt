package org.waitforme.backend.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.waitforme.backend.entity.bookmark.Bookmark
import org.waitforme.backend.model.response.bookmark.BookmarkResponse
import org.waitforme.backend.model.response.bookmark.toResponse
import org.waitforme.backend.repository.bookmark.BookmarkRepository

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
) {
    fun findBookmarkList(userId: Int, pageable: Pageable): Page<BookmarkResponse> {
        return bookmarkRepository.getBookmarkList(
            userId = userId,
            pageable = pageable
        ).map {
            it.toResponse()
        }
    }

    fun updateBookmark(userId: Int, shopId: Int): Boolean {
        val bookmark = bookmarkRepository.findByUserIdAndShopId(userId, shopId)
        val result = bookmark?.let {
            it.isShow = !it.isShow
            it
        } ?: Bookmark(
            userId = userId,
            shopId = shopId,
            isShow = true
        )

        return bookmarkRepository.save(result).isShow
    }
}