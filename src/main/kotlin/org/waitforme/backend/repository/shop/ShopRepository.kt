package org.waitforme.backend.repository.shop

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.Shop

@Repository
interface ShopRepository : CoroutineCrudRepository<Shop, Int> {
}