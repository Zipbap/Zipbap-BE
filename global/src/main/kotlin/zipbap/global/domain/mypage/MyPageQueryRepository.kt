package zipbap.global.domain.mypage

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zipbap.global.domain.bookmark.QBookmark
import zipbap.global.domain.follow.QFollow
import zipbap.global.domain.recipe.QRecipe
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.QUser

@Repository
class MyPageQueryRepository(
        private val query: JPAQueryFactory
) {

    fun loadProfileBlock(ownerId: Long, viewerId: Long): MyPageQueryResult.ProfileBlock {
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
                                MyPageQueryResult.ProfileBlock::class.java,
                                u.id, u.nickname, u.profileImage,
                                followersCount, followingsCount,
                                isFollowingExpr,
                                u.statusMessage,
                                u.isPrivate
                        )
                )
                .from(u)
                .where(u.id.eq(ownerId))
                .fetchOne()
                ?: throw IllegalStateException("User($ownerId) not found")
    }

    fun loadBookmarkCards(ownerId: Long, pageable: Pageable): Page<MyPageQueryResult.RecipeCard> {
        val b = QBookmark.bookmark
        val r = QRecipe.recipe

        val where = BooleanBuilder().apply {
            and(b.user.id.eq(ownerId))
            and(b.deletedAt.isNull)
            and(r.isPrivate.isFalse) // 게시자가 피드에서 비공개로 전환시 조회불가
            and(r.deletedAt.isNull)
        }

        val content = query
                .select(
                        Projections.constructor(
                                MyPageQueryResult.RecipeCard::class.java,
                                r.id, r.title, r.subtitle, r.thumbnail, r.createdAt
                        )
                )
                .from(b)
                .join(b.recipe, r)
                .where(where)
                .orderBy(b.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val total = query
                .select(b.id.count())
                .from(b)
                .join(b.recipe, r)
                .where(where)
                .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun loadFeedCards(ownerId: Long, pageable: Pageable): Page<MyPageQueryResult.RecipeCard> {
        val r = QRecipe.recipe

        val where = BooleanBuilder().apply {
            and(r.user.id.eq(ownerId))
            and(r.recipeStatus.eq(RecipeStatus.ACTIVE))
            and(r.isPrivate.isFalse)
            and(r.deletedAt.isNull)
        }

        val content = query
                .select(
                        Projections.constructor(
                                MyPageQueryResult.RecipeCard::class.java,
                                r.id, r.title, r.subtitle, r.thumbnail, r.createdAt
                        )
                )
                .from(r)
                .where(where)
                .orderBy(r.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val total = query
                .select(r.count())
                .from(r)
                .where(where)
                .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
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