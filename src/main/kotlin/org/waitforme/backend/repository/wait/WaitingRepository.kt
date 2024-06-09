package org.waitforme.backend.repository.wait

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.model.dto.wait.WaitingListOwnerResult
import org.waitforme.backend.model.dto.wait.WaitingListResult

@Repository
interface WaitingRepository : CrudRepository<Waiting, Int>, WaitingCustomRepository {

    @Query(nativeQuery = true, value = WaitingQuery.findOrderNoTop1)
    fun findOrderNoTop1(shopId: Int): Int

    @Query(nativeQuery = true, value = WaitingQuery.findWaitingList)
    fun findWaitingList(shopId: Int, limit: Int, start: Long): List<WaitingListOwnerResult>

    @Query(nativeQuery = true, value = WaitingQuery.countWaitingList)
    fun countWaitingList(shopId: Int): Long

    @Query(nativeQuery = true, value = WaitingQuery.findByUserId)
    fun findByUserId(userId: Int, limit: Int, start: Long): List<WaitingListResult>

    fun countByUserId(userId: Int): Long

    fun findByShopIdAndUserId(shopId: Int, userId: Int): Waiting?

    fun findByShopIdAndPhoneNumber(shopId: Int, phoneNumber: String): Waiting?

    fun findTop1ByShopIdOrderByIdDesc(shopId: Int): Waiting?

    fun findByShopIdAndEntryCode(shopId: Int, entryCode: String): Waiting?

    fun findStatusByPhoneNumberAndShopId(phoneNumber: String, shopId: Int): Waiting?
}
