package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.model.request.wait.AddEntryRequest
import org.waitforme.backend.model.response.wait.WaitingOwnerResponse
import org.waitforme.backend.model.response.wait.WaitingResponse
import org.waitforme.backend.service.WaitingService

@RestController
@Tag(name = "대기 관련 API")
@RequestMapping("/v1/waiting")
class WaitingController(
    private val waitingService: WaitingService
) {
    @GetMapping("/owner/{shopId}")
    fun getWaitingListOwner(
        @Parameter(name = "userId", description = "점주 ID", `in` = ParameterIn.QUERY)
        userId: Int,
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<WaitingOwnerResponse> =
        waitingService.getWaitingListOwner(userId, shopId, PageRequest.of(page ?: 0, size ?: 10))

    @GetMapping("/{userId}")
    fun getWaitingList(
        @Parameter(name = "userId", description = "유저 ID", `in` = ParameterIn.PATH)
        @PathVariable
        userId: Int,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<WaitingResponse> =
        waitingService.getWaitingList(userId, PageRequest.of(page ?: 0, size ?: 10))

    @PostMapping("/{shopId}")
    fun addEntry(
        @PathVariable
        shopId: Int,
        request: AddEntryRequest
    ): Int = waitingService.addEntry(shopId, request)

    @GetMapping("remain/{shopId}")
    fun getRemainCount(
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int
    ): Int = waitingService.getRemainCount(shopId)
}