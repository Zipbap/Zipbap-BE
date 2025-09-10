package zipbap.app.api.mypage.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import zipbap.app.api.mypage.dto.ProfileBlock
import zipbap.app.api.mypage.dto.RecipeCardDto
import zipbap.app.domain.follow.QFollow
import zipbap.app.domain.recipe.QRecipe
import zipbap.app.domain.recipe.RecipeStatus
import zipbap.app.domain.bookmark.QBookmark
import zipbap.app.domain.user.QUser

@Repository
class MyPageQueryRepository(
        private val query: JPAQueryFactory
) {

    fun loadProfileBlock(ownerId: Long, viewerId: Long): ProfileBlock {
        val u = QUser.user
        val f1 = QFollow("f1") // followers
        val f2 = QFollow("f2") // followings
        val f3 = QFollow("f3") // isFollowing exists

        val followersCount = JPAExpressions
                .select(f1.id.count())
                .from(f1)
                .where(f1.following.id.eq(ownerId))

        val followingsCount = JPAExpressions
                .select(f2.id.count())
                .from(f2)
                .where(f2.follower.id.eq(ownerId))

        val isFollowingExpr = JPAExpressions
                .selectOne()
                .from(f3)
                .where(
                        f3.follower.id.eq(viewerId),
                        f3.following.id.eq(ownerId)
                )
                .exists()

        return query
                .select(
                        Projections.constructor(
                                ProfileBlock::class.java,
                                u.id, u.nickname, u.profileImage,
                                followersCount, followingsCount,
                                isFollowingExpr
                        )
                )
                .from(u)
                .where(u.id.eq(ownerId))
                .fetchOne()
                ?: throw IllegalStateException("User($ownerId) not found")
    }

    fun loadBookmarkCards(ownerId: Long, limit: Int): List<RecipeCardDto> {
        val b = QBookmark.bookmark
        val r = QRecipe.recipe

        return query
                .select(
                        Projections.constructor(
                                RecipeCardDto::class.java,
                                r.recipeId, r.title, r.subtitle, r.thumbnail, r.createdAt
                        )
                )
                .from(b)
                .join(b.recipe, r)
                .where(b.user.id.eq(ownerId))
                .orderBy(b.createdAt.desc())
                .limit(limit.toLong())
                .fetch()
    }

    fun loadFeedCards(ownerId: Long, offset: Int, limit: Int): List<RecipeCardDto> {
        val r = QRecipe.recipe

        return query
                .select(
                        Projections.constructor(
                                RecipeCardDto::class.java,
                                r.recipeId, r.title, r.subtitle, r.thumbnail, r.createdAt
                        )
                )
                .from(r)
                .where(
                        r.user.id.eq(ownerId),
                        r.recipeStatus.eq(RecipeStatus.ACTIVE),
                        r.isPrivate.isFalse
                )
                .orderBy(r.createdAt.desc())
                .offset(offset.toLong())
                .limit(limit.toLong())
                .fetch()
    }

    // (선택) 팔로워/팔로잉을 "한 스캔"으로 구하는 버전 (MySQL용 CASE 방식)
    // 호출 지점에서 필요하다면 사용하세요.
    fun countFollowersAndFollowingsInOneScan(ownerId: Long): Pair<Long, Long> {
        val f = QFollow.follow
        val followers = query
                .select(f.id.count())
                .from(f)
                .where(f.following.id.eq(ownerId))
                .fetchOne() ?: 0L

        val followings = query
                .select(f.id.count())
                .from(f)
                .where(f.follower.id.eq(ownerId))
                .fetchOne() ?: 0L

        return followers to followings
    }
}