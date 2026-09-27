package support.query

import zipbap.global.domain.feed.FeedFilterType

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import zipbap.global.domain.bookmark.QBookmark
import zipbap.global.domain.comment.QComment
import zipbap.global.domain.feed.FeedQueryResult.FeedListRow
import zipbap.global.domain.feed.QFeedQueryResult_FeedListRow as QFeedListRow
import zipbap.global.domain.follow.QFollow
import zipbap.global.domain.like.QRecipeLike
import zipbap.global.domain.recipe.QRecipe
import zipbap.global.domain.recipe.RecipeVisibility
import zipbap.global.domain.user.QUser
import zipbap.global.domain.user.User
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

// Frozen pre-C query: keep independent from production aggregation and pagination changes.
class LegacyFeedListQuery(
    entityManager: EntityManager,
    private val clock: Clock
) {
    private val queryFactory = JPAQueryFactory(entityManager)

    private val recipe = QRecipe.recipe
    private val author = QUser.user
    private val like = QRecipeLike.recipeLike
    private val bookmark = QBookmark.bookmark
    private val comment = QComment.comment
    private val follow = QFollow.follow

    private val KST: ZoneId = ZoneId.of("Asia/Seoul")

    fun findFeed(
        loginUser: User?,
        filter: FeedFilterType,
        pageable: Pageable,
        keyword: String?
    ): Page<FeedListRow> {
        val baseVisibility = RecipeVisibility.visibleTo(loginUser?.id, recipe, author)

        val filterCondition = when (filter) {
            FeedFilterType.ALL -> null
            FeedFilterType.TODAY -> todayCondition()
            FeedFilterType.HOT -> null
            FeedFilterType.RECOMMEND -> null
            FeedFilterType.FOLLOWING -> followingOnly(loginUser)
        }

        val keywordCond = searchCondition(keyword)

        var where = if (filterCondition != null) baseVisibility.and(filterCondition) else baseVisibility
        if (keywordCond != null) where = where.and(keywordCond)

        val priority = priorityExpr(keyword)

        val orderSpecifiers = mutableListOf<OrderSpecifier<*>>()
        if (priority != null) orderSpecifiers += priority.desc()

        when (filter) {
            FeedFilterType.HOT -> orderSpecifiers += arrayOf(like.id.countDistinct().desc(), recipe.createdAt.desc())
            FeedFilterType.RECOMMEND -> orderSpecifiers += arrayOf(bookmark.id.countDistinct().desc(), recipe.createdAt.desc())
            else -> orderSpecifiers += arrayOf(recipe.createdAt.desc())
        }

        val content = queryFactory
            .select(
                QFeedListRow(
                    author.id,
                    author.nickname,
                    author.profileImage,
                    author.isPrivate,
                    recipe.id,
                    recipe.title,
                    recipe.thumbnail,
                    recipe.introduction,
                    recipe.cookingTime.cookingTime,
                    recipe.level.level,
                    recipe.createdAt,
                    recipe.updatedAt,
                    like.id.countDistinct(),
                    bookmark.id.countDistinct(),
                    comment.id.countDistinct(),
                    Expressions.FALSE,
                    Expressions.FALSE,
                    recipe.isPrivate,
                    recipe.viewCount
                )
            )
            .from(recipe)
            .join(recipe.user, author)
            .leftJoin(like).on(like.recipe.eq(recipe))
            .leftJoin(bookmark).on(bookmark.recipe.eq(recipe))
            .leftJoin(comment).on(comment.recipe.eq(recipe))
            .where(where)
            .groupBy(recipe.id, author.id)
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(recipe.count())
            .from(recipe)
            .join(recipe.user, author)
            .where(where)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun todayCondition(): BooleanExpression {
        val today = LocalDate.now(clock.withZone(KST))
        val start = today.atStartOfDay()
        val end = today.plusDays(1).atStartOfDay()
        return recipe.createdAt.goe(start).and(recipe.createdAt.lt(end))
    }

    private fun followingOnly(loginUser: User?): BooleanExpression =
        if (loginUser == null) Expressions.FALSE
        else queryFactory.selectOne().from(follow)
            .where(follow.follower.eq(loginUser).and(follow.following.eq(author)))
            .exists()

    private fun searchCondition(keyword: String?): BooleanExpression? =
        keyword?.trim()?.takeIf { it.isNotBlank() }?.let { q ->
            recipe.title.containsIgnoreCase(q)
                .or(recipe.subtitle.containsIgnoreCase(q))
                .or(recipe.ingredientInfo.containsIgnoreCase(q))
        }

    private fun priorityExpr(keyword: String?): NumberExpression<Int>? =
        keyword?.trim()?.takeIf { it.isNotBlank() }?.let { q ->
            CaseBuilder()
                .`when`(recipe.title.containsIgnoreCase(q)).then(3)
                .`when`(recipe.subtitle.containsIgnoreCase(q)).then(2)
                .`when`(recipe.ingredientInfo.containsIgnoreCase(q)).then(1)
                .otherwise(0)
        }
}
