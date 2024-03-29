package org.waitforme.backend.repository.shop

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.Shop

@Repository
interface ShopRepository : CrudRepository<Shop, Int>, ShopCustomRepository {
    fun findByIdAndIsShow(id: Int, isShow: Boolean = true): Shop?
}