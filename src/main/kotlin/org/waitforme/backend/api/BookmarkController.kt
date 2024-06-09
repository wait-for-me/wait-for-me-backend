package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.LoginUser
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
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<BookmarkResponse> =
        bookmarkService.findBookmarkList(loginUser.id, PageRequest.of(page ?: 0, size ?: 10))

    @PostMapping("/{shopId}")
    fun updateBookmark(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "shopId", description = "유저 ID", `in` = ParameterIn.PATH) @PathVariable
        shopId: Int,
    ): Boolean =
        bookmarkService.updateBookmark(loginUser.id, shopId)

}