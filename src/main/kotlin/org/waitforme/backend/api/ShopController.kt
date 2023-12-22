package org.waitforme.backend.api

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    ): ShopDetailResponse =
        shopService.getShopDetail(id)
}