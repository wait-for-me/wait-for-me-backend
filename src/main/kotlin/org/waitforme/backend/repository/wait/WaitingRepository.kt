package org.waitforme.backend.repository.wait

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.wait.Waiting
import org.waitforme.backend.model.dto.wait.WaitingListOwnerResult
import org.waitforme.backend.model.dto.wait.WaitingListResult

@Repository
interface WaitingRepository : CrudRepository<Waiting, Int> {

    @Query(nativeQuery = true, value = WaitingQuery.findOrderNoTop1)
    fun findOrderNoTop1(shopId: Int): Int

    @Query(nativeQuery = true, value = WaitingQuery.findWaitingList)
    fun findWaitingList(shopId: Int, limit: Int, start: Long): List<WaitingListOwnerResult>

    @Query(nativeQuery = true, value = WaitingQuery.countWaitingList)
    fun countWaitingList(shopId: Int): Long

    @Query(nativeQuery = true, value = WaitingQuery.findByUserId)
    fun findByUserId(userId: Int, limit: Int, start: Long): List<WaitingListResult>

    fun countByUserId(userId: Int): Long

    fun findStatusByUserId(userId: Int, shopId: Int): Waiting?
}
