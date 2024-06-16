package org.waitforme.backend.repository.shop

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.QShop
import org.waitforme.backend.entity.shop.QShopImage
import org.waitforme.backend.entity.user.QUser
import org.waitforme.backend.enums.ShopSorter
import org.waitforme.backend.model.dto.shop.OwnerShopListResultDto
import org.waitforme.backend.model.dto.shop.ShopListResultDto
import java.time.LocalDate

@Repository
class ShopCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): ShopCustomRepository {
    private val shop = QShop.shop
    private val shopImage = QShopImage.shopImage
    private val user = QUser.user

    override fun findShopList(
        title: String?,
        startedAt: LocalDate,
        endedAt: LocalDate,
        sorter: ShopSorter,
        pageable: Pageable
    ): Page<ShopListResultDto> {
        val orderList = mutableListOf<OrderSpecifier<*>>()
        when (sorter) {
            ShopSorter.NEWEST -> {
                orderList.add(OrderSpecifier(Order.DESC, shop.startedAt))
                orderList.add(OrderSpecifier(Order.DESC, shop.endedAt))
            }
            ShopSorter.DEADLINE -> {
                orderList.add(OrderSpecifier(Order.ASC, shop.endedAt))
            }
        }
        val content = queryFactory
            .select(
                Projections.fields(
                    ShopListResultDto::class.java,
                    shop.id,
                    ExpressionUtils.`as`(shop.name, "title"),
                    shopImage.imagePath,
                    shop.endedAt
                )
            )
            .from(shop)
            .innerJoin(shopImage).on(shop.id.eq(shopImage.shopId))
            .where(
                shop.isDeleted.isFalse,
                shop.isShow.isTrue,
                shop.startedAt.goe(startedAt),
                shop.endedAt.lt(endedAt),
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null
            )
            .orderBy(*orderList.toTypedArray())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        val count = queryFactory
            .select(shop.count())
            .from(shop)
            .innerJoin(shopImage).on(shop.id.eq(shopImage.shopId))
            .where(
                shop.isDeleted.isFalse,
                shop.isShow.isTrue,
                shop.startedAt.goe(startedAt),
                shop.endedAt.lt(endedAt),
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null
            )
            .fetchOne() ?: 0

        return PageImpl(content, pageable, count)
    }

    override fun findOwnerShopList(
        userId: Int,
        title: String?,
        startedAt: LocalDate,
        endedAt: LocalDate,
        isEnd: Boolean,
        pageable: Pageable
    ): Page<OwnerShopListResultDto> {
        val content = queryFactory
            .select(
                Projections.fields(
                    OwnerShopListResultDto::class.java,
                    shop.id,
                    ExpressionUtils.`as`(shop.name, "title"),
                    shop.startedAt,
                    shop.endedAt,
                    shopImage.imagePath,
                    shop.registrationNumber
                )
            )
            .from(shop)
            .innerJoin(shopImage).on(shop.id.eq(shopImage.shopId))
            .innerJoin(user).on(shop.userId.eq(user.id))
            .where(
                shop.isDeleted.isFalse,
                if (isEnd) {
                    shop.endedAt.goe(endedAt)
                        .or(shop.isShow.eq(false))
                } else {
                    shop.isShow.isTrue
                        .and(shop.startedAt.goe(startedAt))
                        .and(shop.endedAt.lt(endedAt))
                },
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null,
                shop.userId.eq(userId),
                user.isOwner.isTrue
            )
            .orderBy(
                shop.startedAt.asc(),
                shop.endedAt.desc()
            )
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        val count = queryFactory
            .select(shop.count())
            .from(shop)
            .innerJoin(shopImage).on(shop.id.eq(shopImage.shopId))
            .innerJoin(user).on(shop.userId.eq(user.id))
            .where(
                shop.isDeleted.isFalse,
                if (isEnd) {
                    shop.endedAt.goe(endedAt)
                        .or(shop.isShow.eq(false))
                } else {
                    shop.isShow.isTrue
                        .and(shop.startedAt.goe(startedAt))
                        .and(shop.endedAt.lt(endedAt))
                },
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null,
                shop.userId.eq(userId),
                user.isOwner.isTrue
            )
            .fetchOne() ?: 0

        return PageImpl(content, pageable, count)
    }
}