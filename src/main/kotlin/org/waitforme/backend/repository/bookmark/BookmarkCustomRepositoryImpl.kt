package org.waitforme.backend.repository.bookmark

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions.select
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.bookmark.QBookmark
import org.waitforme.backend.entity.shop.QShop
import org.waitforme.backend.model.dto.bookmark.BookmarkResultDto

@Repository
class BookmarkCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): BookmarkCustomRepository {
    private val bookmark = QBookmark.bookmark
    private val shop = QShop.shop

    override fun getBookmarkList(userId: Int, pageable: Pageable): Page<BookmarkResultDto> {
        val content = queryFactory
            .select(
                Projections.fields(
                    BookmarkResultDto::class.java,
                    ExpressionUtils.`as`(shop.id, "shopId"),
                    ExpressionUtils.`as`(shop.name, "shopName"),
                    shop.startedAt,
                    shop.endedAt,
                    shop.openedAt,
                    shop.closedAt,
                    bookmark.updatedAt
                )
            )
            .from(bookmark)
            .innerJoin(shop).on(bookmark.shopId.eq(shop.id))
            .where(
                bookmark.userId.eq(userId),
                bookmark.isShow.isTrue
            )
            .orderBy(bookmark.updatedAt.desc())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        val count = queryFactory
            .select(bookmark.count())
            .from(bookmark)
            .innerJoin(shop).on(bookmark.shopId.eq(shop.id))
            .where(
                bookmark.userId.eq(userId),
                bookmark.isShow.isTrue
            )
            .fetchOne()

        return PageImpl(content, pageable, count ?: 0)
    }
}