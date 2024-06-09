package org.waitforme.backend.repository.wait

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.ExpressionUtils.count
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.wait.QWaiting
import org.waitforme.backend.enums.EntryStatus
import org.waitforme.backend.model.dto.wait.WaitingMembersResult

@Repository
class WaitingCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): WaitingCustomRepository {
    private val waiting = QWaiting.waiting
    private val waiting2 = QWaiting.waiting
    override fun countWaitingMembers(
        userId: Int?,
        shopId: Int,
        password: String,
        phoneNumber: String
    ): WaitingMembersResult? {
         return queryFactory.select(
             Projections.fields(
                 WaitingMembersResult::class.java,
                 waiting.orderNo,
                 ExpressionUtils.`as`(
                     JPAExpressions.select(
                         count(waiting2.id))
                         .from(waiting2)
                         .where(
                             waiting2.shopId.eq(shopId),
                             waiting2.orderNo.lt(waiting.orderNo),
                             waiting2.status.eq(EntryStatus.WAIT)
                         ),
                     "remainCount")
             )
         )
             .from(waiting)
             .where(
                 waiting.shopId.eq(shopId),
                 userId?.let { waiting.userId.eq(userId) },
                 waiting.password.eq(password),
                 waiting.phoneNumber.eq(phoneNumber),
              )
             .fetchFirst()
    }
}