package org.waitforme.backend.repository.shop

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.model.dto.ShopListResultDto
import java.time.LocalDate

@Repository
interface ShopRepository : CoroutineCrudRepository<Shop, Int> {

    @Query("SELECT s.id, s.name, si.imagePath " +
            "FROM Shop as s " +
            "INNER JOIN ShopImage as si ON s.id = si.shopId " +
            "WHERE s.startedAt < :openedAt " +
            "AND s.endedAt >= :endedAt " +
            "AND s.isShow = :isShow " +
            "AND si.imageType = 'MAIN' " +
            "AND s.isDeleted = false "
    )
    suspend fun findShopList(
        startedAt: LocalDate,
        endedAt: LocalDate,
        isShow: Boolean = true,
        pageable: Pageable
    ): List<ShopListResultDto>

    suspend fun findByIdAndIsShow(id: Int, isShow: Boolean = true): Shop?
}