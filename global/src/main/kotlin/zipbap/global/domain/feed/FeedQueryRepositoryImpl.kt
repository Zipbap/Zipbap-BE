package zipbap.global.domain.feed

import com.querydsl.core.types.OrderSpecifier
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
import zipbap.global.domain.category.cookingtime.QCookingTime
import zipbap.global.domain.category.level.QLevel
import zipbap.global.domain.comment.QComment
import zipbap.global.domain.feed.FeedQueryResult.FeedDetailRow
import zipbap.global.domain.feed.FeedQueryResult.FeedListRow
import zipbap.global.domain.feed.QFeedQueryResult_FeedDetailRow as QFeedDetailRow
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

@Repository
class FeedQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val clock: Clock
) : FeedQueryRepository {

    private val recipe = QRecipe.recipe
    private val author = QUser.user
    private val like = QRecipeLike.recipeLike
    private val bookmark = QBookmark.bookmark
    private val comment = QComment.comment
    private val follow = QFollow.follow
    private val cookingTime = QCookingTime("feedCookingTime")
    private val level = QLevel("feedLevel")

    private val KST: ZoneId = ZoneId.of("Asia/Seoul")

    override fun findFeed(
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
            FeedFilterType.HOT -> orderSpecifiers += arrayOf(like.id.count().desc(), recipe.createdAt.desc())
            FeedFilterType.RECOMMEND -> orderSpecifiers += arrayOf(bookmark.id.count().desc(), recipe.createdAt.desc())
            else -> orderSpecifiers += arrayOf(recipe.createdAt.desc())
        }
        // A unique final key keeps ties stable across OFFSET page boundaries on unchanged data.
        orderSpecifiers += recipe.id.desc()

        // Rank over every visible candidate before LIMIT. Join only the relation used for ranking.
        val pageQuery = queryFactory
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
                    cookingTime.cookingTime,
                    level.level,
                    recipe.createdAt,
                    recipe.updatedAt,
                    if (filter == FeedFilterType.HOT) like.id.count() else Expressions.asNumber(0L),
                    if (filter == FeedFilterType.RECOMMEND) bookmark.id.count() else Expressions.asNumber(0L),
                    Expressions.asNumber(0L),
                    Expressions.FALSE,
                    Expressions.FALSE,
                    recipe.isPrivate,
                    recipe.viewCount
                )
            )
            .from(recipe)
            .join(recipe.user, author)
            // Preserve the previous projection's implicit inner-join eligibility before pagination.
            .join(recipe.cookingTime, cookingTime)
            .join(recipe.level, level)
            .where(where)

        when (filter) {
            FeedFilterType.HOT -> pageQuery.leftJoin(like).on(like.recipe.eq(recipe))
                .groupBy(recipe.id, author.id)
            FeedFilterType.RECOMMEND -> pageQuery.leftJoin(bookmark).on(bookmark.recipe.eq(recipe))
                .groupBy(recipe.id, author.id)
            else -> Unit
        }

        val rows = pageQuery
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

        val recipeIds = rows.mapNotNull { it.recipeId }
        if (recipeIds.isEmpty()) return PageImpl(emptyList(), pageable, total)

        // Page-bounded aggregates avoid the likes x bookmarks x comments intermediate result.
        val likeCounts = if (filter == FeedFilterType.HOT) emptyMap() else countLikes(recipeIds)
        val bookmarkCounts = if (filter == FeedFilterType.RECOMMEND) emptyMap() else countBookmarks(recipeIds)
        val commentCounts = countComments(recipeIds)
        val content = rows.map { row ->
            row.copy(
                likeCount = if (filter == FeedFilterType.HOT) row.likeCount else likeCounts[row.recipeId] ?: 0L,
                bookmarkCount = if (filter == FeedFilterType.RECOMMEND) row.bookmarkCount else bookmarkCounts[row.recipeId] ?: 0L,
                commentCount = commentCounts[row.recipeId] ?: 0L
            )
        }
        return PageImpl(content, pageable, total)
    }

    private fun countLikes(recipeIds: List<String>): Map<String, Long> {
        val count = like.id.count()
        return queryFactory.select(like.recipe.id, count).from(like)
            .where(like.recipe.id.`in`(recipeIds)).groupBy(like.recipe.id).fetch()
            .associate { it.get(like.recipe.id)!! to it.get(count)!! }
    }

    private fun countBookmarks(recipeIds: List<String>): Map<String, Long> {
        val count = bookmark.id.count()
        return queryFactory.select(bookmark.recipe.id, count).from(bookmark)
            .where(bookmark.recipe.id.`in`(recipeIds)).groupBy(bookmark.recipe.id).fetch()
            .associate { it.get(bookmark.recipe.id)!! to it.get(count)!! }
    }

    private fun countComments(recipeIds: List<String>): Map<String, Long> {
        val count = comment.id.count()
        return queryFactory.select(comment.recipe.id, count).from(comment)
            .where(comment.recipe.id.`in`(recipeIds)).groupBy(comment.recipe.id).fetch()
            .associate { it.get(comment.recipe.id)!! to it.get(count)!! }
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

        val followerCountExpr = queryFactory
            .select(follow.id.countDistinct())
            .from(follow)
            .where(follow.following.eq(author))

        return queryFactory
            .select(
                QFeedDetailRow(
                    author.id,
                    author.nickname,
                    author.profileImage,
                    author.statusMessage,
                    isFollowingExpr,
                    recipe.id,
                    recipe.title,
                    recipe.subtitle,
                    recipe.introduction,
                    recipe.thumbnail,
                    recipe.video,
                    recipe.ingredientInfo,
                    recipe.kick,
                    recipe.isPrivate,
                    recipe.recipeStatus.stringValue(),
                    recipe.cookingType.type,
                    recipe.situation.situation,
                    recipe.mainIngredient.ingredient,
                    recipe.method.method,
                    recipe.headcount.headcount,
                    recipe.cookingTime.cookingTime,
                    recipe.level.level,
                    recipe.myCategory.name,
                    followerCountExpr,
                    recipe.createdAt,
                    recipe.updatedAt,
                    like.id.countDistinct(),
                    bookmark.id.countDistinct(),
                    comment.id.countDistinct(),
                    Expressions.FALSE,
                    Expressions.FALSE,
                    recipe.viewCount,
                    Expressions.FALSE
                )
            )
            .from(recipe)
            .join(recipe.user, author)
            .leftJoin(recipe.myCategory)
            .leftJoin(like).on(like.recipe.eq(recipe))
            .leftJoin(bookmark).on(bookmark.recipe.eq(recipe))
            .leftJoin(comment).on(comment.recipe.eq(recipe))
            .where(
                recipe.id.eq(recipeId)
                    .and(RecipeVisibility.visibleTo(loginUser?.id, recipe, author))
            )
            .groupBy(recipe.id, author.id)
            .fetchOne()
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
