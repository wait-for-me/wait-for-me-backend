package org.waitforme.backend.repository.shop

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.enums.ImageType

@Repository
interface ShopImageRepository : CrudRepository<ShopImage, Int> {

    fun findByShopIdAndIsShowOrderByOrderNo(shopId: Int, isShow: Boolean = true): List<ShopImage>

    @Modifying
    @Query(value = "UPDATE ShopImage si SET si.isShow = false " +
            "WHERE si.shopId = :shopId AND si.imageType = :type AND si.isShow = :isShow")
    fun updateByShopIdAndTypeAndIsShow(shopId: Int, type: ImageType, isShow: Boolean = true): Boolean
}