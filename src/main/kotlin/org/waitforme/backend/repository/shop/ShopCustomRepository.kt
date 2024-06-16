package org.waitforme.backend.repository.shop

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.waitforme.backend.enums.ShopSorter
import org.waitforme.backend.model.dto.shop.OwnerShopListResultDto
import org.waitforme.backend.model.dto.shop.ShopListResultDto
import java.time.LocalDate

interface ShopCustomRepository {
    fun findShopList(
        userId: Int?,
        title: String?,
        startedAt: LocalDate,
        endedAt: LocalDate,
        sorter: ShopSorter,
        pageable: Pageable
    ): Page<ShopListResultDto>

    fun findOwnerShopList(
        userId: Int,
        title: String?,
        startedAt: LocalDate,
        endedAt: LocalDate,
        isEnd: Boolean = true,
        pageable: Pageable
    ): Page<OwnerShopListResultDto>
}