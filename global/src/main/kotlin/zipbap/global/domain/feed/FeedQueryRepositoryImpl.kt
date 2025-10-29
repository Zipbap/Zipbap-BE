package zipbap.global.domain.feed

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zipbap.global.domain.bookmark.QBookmark
import zipbap.global.domain.comment.QComment
import zipbap.global.domain.comment.QComment.*
import zipbap.global.domain.feed.FeedQueryResult.FeedDetailRow
import zipbap.global.domain.feed.FeedQueryResult.FeedListRow
import zipbap.global.domain.follow.QFollow
import zipbap.global.domain.like.QRecipeLike
import zipbap.global.domain.recipe.QRecipe
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.QUser
import zipbap.global.domain.user.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * FeedQueryRepositoryImpl
 *
 * - QueryDSL을 통한 피드 목록/상세 조회 구현체
 */
@Repository
class FeedQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FeedQueryRepository {

    private val recipe = QRecipe.recipe
    private val author = QUser.user
    private val like = QRecipeLike.recipeLike
    private val bookmark = QBookmark.bookmark
    private val comment = QComment.comment
    private val follow = QFollow.follow

    private val KST: ZoneId = ZoneId.of("Asia/Seoul")

    override fun findFeed(
            loginUser: User?,
            filter: FeedFilterType,
            pageable: Pageable,
            keyword: String?
    ): Page<FeedListRow> {
        val baseVisibility = activeRecipe()
            .and(recipe.isPrivate.isFalse)
            .and(authorVisibility(loginUser))

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
        if (priority != null) {
            orderSpecifiers += priority.desc()
        }
        when (filter) {
            FeedFilterType.HOT -> orderSpecifiers += arrayOf(like.id.countDistinct().desc(), recipe.createdAt.desc())
            FeedFilterType.RECOMMEND -> orderSpecifiers += arrayOf(bookmark.bookmarkId.countDistinct().desc(), recipe.createdAt.desc())
            else -> orderSpecifiers += arrayOf(recipe.createdAt.desc())
        }

        val query = queryFactory
            .select(
                Projections.fields(
                    FeedListRow::class.java,
                    author.nickname.`as`("nickname"),
                    author.profileImage.`as`("profileImage"),
                    author.isPrivate.`as`("userIsPrivate"),
                    recipe.recipeId.`as`("recipeId"),
                    recipe.title.`as`("title"),
                    recipe.thumbnail.`as`("thumbnail"),
                    recipe.introduction.`as`("introduction"),
                    recipe.cookingTime.cookingTime.`as`("cookingTime"),
                    recipe.level.level.`as`("level"),
                    recipe.createdAt.`as`("createdAt"),
                    recipe.updatedAt.`as`("updatedAt"),
                    like.id.countDistinct().`as`("likeCount"),
                    bookmark.bookmarkId.countDistinct().`as`("bookmarkCount"),
                    comment.id.countDistinct().`as`("commentCount")
                )
            )
            .from(recipe)
            .join(recipe.user, author)
            .leftJoin(like).on(like.recipe.eq(recipe))
            .leftJoin(bookmark).on(bookmark.recipe.eq(recipe))
            .leftJoin(comment).on(comment.recipe.eq(recipe))
            .where(where)
            .groupBy(recipe.recipeId, author.id)
            .orderBy(*orderSpecifiers.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val content = query.fetch()

        val total = queryFactory
            .select(recipe.count())
            .from(recipe)
            .join(recipe.user, author)
            .where(where)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findFeedDetail(
            loginUser: User?,
            recipeId: String
    ): FeedDetailRow? {
        val isFollowingExpr = if (loginUser != null) {
            queryFactory.selectOne().from(follow)
                .where(follow.follower.eq(loginUser).and(follow.following.eq(author)))
                .exists()
        } else Expressions.FALSE

        return queryFactory
            .select(
                Projections.fields(
                    FeedDetailRow::class.java,
                    author.nickname.`as`("nickname"),
                    author.profileImage.`as`("profileImage"),
                    author.statusMessage.`as`("statusMessage"),
                    isFollowingExpr.`as`("isFollowing"),
                    recipe.recipeId.`as`("recipeId"),
                    recipe.title.`as`("title"),
                    recipe.subtitle.`as`("subtitle"),
                    recipe.introduction.`as`("introduction"),
                    recipe.thumbnail.`as`("thumbnail"),
                    recipe.video.`as`("video"),
                    recipe.ingredientInfo.`as`("ingredientInfo"),
                    recipe.kick.`as`("kick"),
                    recipe.isPrivate.`as`("recipeIsPrivate"),
                    recipe.recipeStatus.stringValue().`as`("recipeStatus"),
                    recipe.cookingType.type.`as`("cookingType"),
                    recipe.situation.situation.`as`("situation"),
                    recipe.mainIngredient.ingredient.`as`("mainIngredient"),
                    recipe.method.method.`as`("method"),
                    recipe.headcount.headcount.`as`("headcount"),
                    recipe.cookingTime.cookingTime.`as`("cookingTime"),
                    recipe.level.level.`as`("level"),
                    recipe.createdAt.`as`("createdAt"),
                    recipe.updatedAt.`as`("updatedAt"),
                    like.id.countDistinct().`as`("likeCount"),
                    bookmark.bookmarkId.countDistinct().`as`("bookmarkCount"),
                    comment.id.countDistinct().`as`("commentCount")
                )
            )
            .from(recipe)
            .join(recipe.user, author)
            .leftJoin(like).on(like.recipe.eq(recipe))
            .leftJoin(bookmark).on(bookmark.recipe.eq(recipe))
            .leftJoin(comment).on(comment.recipe.eq(recipe))
            .where(
                recipe.recipeId.eq(recipeId)
                    .and(activeRecipe())
                    .and(recipe.isPrivate.isFalse.or(loginUserOwns(loginUser)).or(authorVisibility(loginUser)))
            )
            .groupBy(recipe.recipeId, author.id)
            .fetchOne()
    }

    private fun activeRecipe(): BooleanExpression =
        recipe.recipeStatus.eq(RecipeStatus.ACTIVE)

    private fun authorVisibility(loginUser: User?): BooleanExpression {
        val publicAuthor = author.isPrivate.isFalse
        val followed = if (loginUser != null) {
            queryFactory.selectOne()
                .from(follow)
                .where(follow.follower.eq(loginUser).and(follow.following.eq(author)))
                .exists()
        } else Expressions.FALSE
        return publicAuthor.or(followed).or(loginUserOwns(loginUser))
    }

    private fun loginUserOwns(loginUser: User?): BooleanExpression =
        if (loginUser != null) author.id.eq(loginUser.id) else Expressions.FALSE

    private fun todayCondition(): BooleanExpression {
        val today = LocalDate.now(KST)
        val start: LocalDateTime = today.atStartOfDay()
        val end: LocalDateTime = today.atTime(LocalTime.MAX)
        return recipe.createdAt.between(start, end)
    }

    private fun followingOnly(loginUser: User?): BooleanExpression {
        if (loginUser == null) return Expressions.FALSE
        return queryFactory.selectOne().from(follow)
            .where(follow.follower.eq(loginUser).and(follow.following.eq(author)))
            .exists()
    }

    private fun searchCondition(keyword: String?): BooleanExpression? {
        if (keyword.isNullOrBlank()) return null
        val q = keyword.trim()

        val titleMatch = recipe.title.containsIgnoreCase(q)
        val subtitleMatch = recipe.subtitle.containsIgnoreCase(q)
        val ingredientMatch = recipe.ingredientInfo.containsIgnoreCase(q)

        return titleMatch.or(subtitleMatch).or(ingredientMatch)
    }

    private fun priorityExpr(keyword: String?): NumberExpression<Int>? {
        if (keyword.isNullOrBlank()) return null
        val q = keyword.trim()

        return CaseBuilder()
                .`when`(recipe.title.containsIgnoreCase(q)).then(3)
                .`when`(recipe.subtitle.containsIgnoreCase(q)).then(2)
                .`when`(recipe.ingredientInfo.containsIgnoreCase(q)).then(1)
                .otherwise(0)
    }
}
