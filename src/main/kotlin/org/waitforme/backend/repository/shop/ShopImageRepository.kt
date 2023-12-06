package org.waitforme.backend.repository.shop

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.ShopImage

@Repository
interface ShopImageRepository : CrudRepository<ShopImage, Int> {

    fun findByShopIdAndIsShowOrderByOrderNo(shopId: Int, isShow: Boolean = true): List<ShopImage>

}