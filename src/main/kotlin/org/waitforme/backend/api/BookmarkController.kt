package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.model.response.bookmark.BookmarkResponse
import org.waitforme.backend.service.BookmarkService


@RestController
@Tag(name = "북마크")
@RequestMapping("/v1/bookmark")
class BookmarkController(
    private val bookmarkService: BookmarkService
) {
    @GetMapping("")
    fun getBookmarkList(
        @Parameter(name = "userId", description = "유저 ID", `in` = ParameterIn.QUERY)
        userId: Int,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<BookmarkResponse> =
        bookmarkService.findBookmarkList(userId, PageRequest.of(page ?: 0, size ?: 10))

}