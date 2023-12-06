package org.waitforme.backend.repository.shop

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.Shop
import org.waitforme.backend.model.dto.ShopListResultDto
import java.time.LocalDate

@Repository
interface ShopRepository : CrudRepository<Shop, Int> {

    @Query("SELECT s.id, s.name, si.imagePath " +
            "FROM Shop as s " +
            "INNER JOIN ShopImage as si ON s.id = si.shopId " +
            "WHERE s.startedAt < ?1 " +
            "AND s.endedAt >= ?2 " +
            "AND s.isShow = ?3 " +
            "AND si.imageType = 'MAIN' " +
            "AND s.isDeleted = false "
    )
    fun findShopList(
        startedAt: LocalDate,
        endedAt: LocalDate,
        isShow: Boolean = true,
        pageable: Pageable
    ): List<ShopListResultDto>

    fun findByIdAndIsShow(id: Int, isShow: Boolean = true): Shop?
}