package org.waitforme.backend.repository.shop

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.shop.QShop
import org.waitforme.backend.entity.shop.QShopImage
import org.waitforme.backend.model.dto.shop.OwnerShopListResultDto
import org.waitforme.backend.model.dto.shop.ShopListResultDto
import java.time.LocalDate

@Repository
class ShopCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): ShopCustomRepository {
    private val shop = QShop.shop
    private val shopImage = QShopImage.shopImage
//    private val user = QUser.user

    override fun findShopList(
        title: String?,
        startedAt: LocalDate,
        endedAt: LocalDate,
        pageable: Pageable
    ): Page<ShopListResultDto> {
        val content = queryFactory
            .select(
                Projections.fields(
                    ShopListResultDto::class.java,
                    shop.id,
                    ExpressionUtils.`as`(shop.name, "title"),
                    shopImage.imagePath
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
        isShow: Boolean,
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
//            .innerJoin(user).on(shop.userId.eq(user.id))
            .where(
                shop.isDeleted.isFalse,
                shop.isShow.eq(isShow),
                shop.startedAt.goe(startedAt),
                shop.endedAt.lt(endedAt),
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null,
                shop.userId.eq(userId),
//                user.isOwner.isTrue
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
//            .innerJoin(user).on(shop.userId.eq(user.id))
            .where(
                shop.isDeleted.isFalse,
                shop.isShow.eq(isShow),
                shop.startedAt.goe(startedAt),
                shop.endedAt.lt(endedAt),
                if (!title.isNullOrEmpty()) shop.name.contains(title) else null,
                shop.userId.eq(userId),
//                user.isOwner.isTrue
            )
            .fetchOne() ?: 0

        return PageImpl(content, pageable, count)
    }
}