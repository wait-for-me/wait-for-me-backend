package org.waitforme.backend.repository.wait

import org.springframework.stereotype.Repository
import org.waitforme.backend.model.dto.wait.WaitingMembersResult

@Repository
interface WaitingCustomRepository {
    fun countWaitingMembers(
        userId: Int?,
        shopId: Int,
        password: String,
        phoneNumber: String,
    ): WaitingMembersResult?
}