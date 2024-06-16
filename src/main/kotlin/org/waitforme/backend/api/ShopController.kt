package org.waitforme.backend.api

import com.amazonaws.services.ec2.model.DefaultRouteTableAssociationValue
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.waitforme.backend.enums.ShopSorter
import org.waitforme.backend.model.LoginUser
import org.waitforme.backend.model.request.CreateShopRequest
import org.waitforme.backend.model.request.UpdateShopRequest
import org.waitforme.backend.model.response.shop.FrontShopDetailResponse
import org.waitforme.backend.model.response.shop.OwnerShopListResponse
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
        @Parameter(name = "title", description = "제목", `in` = ParameterIn.QUERY)
        title: String? = null,
        @Parameter(name = "sorter", description = "정렬(NEWEST, DEADLINE)", `in` = ParameterIn.QUERY)
        sorter: ShopSorter? = ShopSorter.NEWEST,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<ShopListResponse> =
        shopService.getShopList(title, sorter ?: ShopSorter.NEWEST, PageRequest.of(page ?: 0, size ?: 10))

    @GetMapping("/{id}")
    fun getShopDetail(
        @Parameter(name = "id", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int,
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser
    ): FrontShopDetailResponse =
        shopService.getShopDetail(id)

    @GetMapping("/owner")
    fun getOwnerShopList(
        @Parameter(hidden = true) @AuthenticationPrincipal
        loginUser: LoginUser,
        @Parameter(name = "title", description = "제목", `in` = ParameterIn.QUERY)
        title: String? = null,
        @Parameter(name = "isEnd", description = "종료 여부", `in` = ParameterIn.QUERY)
        isEnd: Boolean = true,
        @Parameter(name = "page", description = "0페이지부터 시작", `in` = ParameterIn.QUERY)
        page: Int? = 0,
        @Parameter(name = "size", description = "1페이지 당 크기", `in` = ParameterIn.QUERY)
        size: Int? = 10,
    ): Page<OwnerShopListResponse> =
        shopService.getOwnerShopList(loginUser, title, isEnd, PageRequest.of(page ?: 0, size ?: 10))

    // BACKOFFICE
    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(description = "BACKOFFICE - 팝업스토어 등록")
    fun createShop(
        // TODO: 관리자 체크 로직 추가
        @ModelAttribute
        shopRequest: CreateShopRequest,
    ): ShopDetailResponse =
        shopService.createShop(shopRequest)

    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(description = "BACKOFFICE - 팝업스토어 수정")
    fun updateShop(
        @Parameter(name = "id", description = "팝업 스토어 id", `in` = ParameterIn.PATH)
        @PathVariable id: Int,
        @ModelAttribute
        shopRequest: UpdateShopRequest,
    ): ShopDetailResponse =
        shopService.updateShop(id, shopRequest)


    @PutMapping("/show/{id}")
    fun changeExposure(
        @Parameter(name = "id", description = "팝업 ID", `in` = ParameterIn.PATH)
        @PathVariable
        id: Int,
        @Parameter(name = "isShow", description = "노출 상태", `in` = ParameterIn.QUERY)
        isShow: Boolean
    ): Boolean = shopService.changeExposure(id, isShow)
}