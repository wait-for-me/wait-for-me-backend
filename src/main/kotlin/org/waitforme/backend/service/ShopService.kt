package org.waitforme.backend.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.waitforme.backend.model.response.shop.ShopDetailResponse
import org.waitforme.backend.model.response.shop.ShopListResponse
import org.waitforme.backend.model.response.shop.toDetailResponse
import org.waitforme.backend.model.response.shop.toResponse
import org.waitforme.backend.repository.shop.ShopImageRepository
import org.waitforme.backend.repository.shop.ShopRepository
import org.webjars.NotFoundException
import java.time.LocalDate


@Service
class ShopService(
    private val shopRepository: ShopRepository,
    private val shopImageRepository: ShopImageRepository,
) {
    suspend fun getShopList(pageRequest: PageRequest): List<ShopListResponse> {
        val now = LocalDate.now()
        return shopRepository.findShopList(
            startedAt = now,
            endedAt = now,
            pageable = pageRequest
        ).map { it.toResponse() }
    }

    suspend fun getShopDetail(id: Int): ShopDetailResponse {
        return shopRepository.findByIdAndIsShow(id)?.let { shop ->
            val images = shopImageRepository.findByShopIdAndIsShowOrderByOrderNo(shop.id)
                .map { it.toResponse() }
            shop.toDetailResponse(images)
        } ?: throw NotFoundException("팝업을 찾을 수 없습니다.")
    }
}