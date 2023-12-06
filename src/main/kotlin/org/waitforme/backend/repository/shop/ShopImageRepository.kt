package org.waitforme.backend.repository.shop

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.ShopImage

@Repository
interface ShopImageRepository : CoroutineCrudRepository<ShopImage, Int> {

    suspend fun findByShopIdAndIsShowOrderByOrderNo(shopId: Int, isShow: Boolean = true): List<ShopImage>

}