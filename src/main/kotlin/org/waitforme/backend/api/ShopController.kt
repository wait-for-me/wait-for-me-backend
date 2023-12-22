package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jdk.jfr.ContentType
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.model.request.CreateShopRequest
import org.waitforme.backend.model.response.shop.FrontShopDetailResponse
import org.waitforme.backend.model.response.shop.ShopDetailResponse
import org.waitforme.backend.model.response.shop.ShopListResponse
import org.waitforme.backend.service.ShopService

@RestController
@Tag(name = "POP UP 스토어")
@RequestMapping("/v1/shop")
class ShopController(
    private val shopService: ShopService
) {
    @GetMapping("")
    fun getShopList(
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): List<ShopListResponse> =
        shopService.getShopList(PageRequest.of(page ?: 0, size ?: 10))

    @GetMapping("/{id}")
    fun getShopDetail(
        @Parameter(name = "id", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int
    ): FrontShopDetailResponse =
        shopService.getShopDetail(id)

    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(description = "BACKOFFICE - 팝업스토어 등록")
    fun createShop(
        // TODO: 관리자 체크 로직 추가
        @ModelAttribute
        shopRequest: CreateShopRequest,
    ): ShopDetailResponse =
        shopService.createShop(shopRequest)
}