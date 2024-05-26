package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.waitforme.backend.model.LoginUser
import org.waitforme.backend.model.request.wait.AddEntryRequest
import org.waitforme.backend.model.request.wait.CancelWaitingRequest
import org.waitforme.backend.model.request.wait.CheckStatusRequest
import org.waitforme.backend.model.response.wait.WaitingOwnerResponse
import org.waitforme.backend.model.response.wait.WaitingResponse
import org.waitforme.backend.service.WaitingService
import org.waitforme.backend.util.SqsUtil

@RestController
@Tag(name = "대기 관련 API")
@RequestMapping("/v1/waiting")
class WaitingController(
    private val waitingService: WaitingService,
    private val sqsUtil: SqsUtil
) {
    @GetMapping("/owner/{shopId}")
    fun getWaitingListOwner(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<WaitingOwnerResponse> =
        waitingService.getWaitingListOwner(loginUser.id, shopId, PageRequest.of(page ?: 0, size ?: 10))

    @GetMapping("")
    fun getWaitingList(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<WaitingResponse> =
        waitingService.getWaitingList(loginUser.id, PageRequest.of(page ?: 0, size ?: 10))

    @PostMapping("/{shopId}")
    fun addEntry(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser?,
        @PathVariable
        shopId: Int,
        request: AddEntryRequest
    ) = waitingService.addEntry(shopId, loginUser?.id, request)

    @GetMapping("/remain/{shopId}")
    fun getRemainCount(
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int
    ): Int = waitingService.getRemainCount(shopId)

    @PutMapping("/cancel/{shopId}")
    fun cancelWaiting(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "shopId", description = "SHOP ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int,
        @RequestBody
        request: CancelWaitingRequest
    ): Boolean = waitingService.cancelWaiting(loginUser.id, shopId, request)

    @GetMapping("/code/{shopId}")
    fun createCode(
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int
    ): String = waitingService.createCode(shopId)

    @PostMapping("/status/{shopId}")
    fun checkStatus(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser?,
        @Parameter(name = "shopId", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        shopId: Int,
        @RequestBody request: CheckStatusRequest
    ) = waitingService.checkStatus(loginUser?.id, shopId, request)
}