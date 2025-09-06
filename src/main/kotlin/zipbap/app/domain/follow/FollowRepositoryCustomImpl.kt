package zipbap.app.domain.follow

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import zipbap.app.domain.follow.QFollow.*
import zipbap.app.domain.user.QUser
import zipbap.app.domain.user.QUser.*
import zipbap.app.domain.user.User

class FollowRepositoryCustomImpl(
        private val queryFactory: JPAQueryFactory
): FollowRepositoryCustom {

    override fun searchFollowingList(userId: Long, searchCondition: String?): List<Follow> {
        val builder = BooleanBuilder()
        builder.and(follow.follower.id.eq(userId))

        if (!searchCondition.isNullOrBlank()) {
            builder.and(follow.following.nickname.like("$searchCondition%"))
        }


        return queryFactory.select(follow)
                .from(follow)
                .where(builder)
                .fetch()
    }

    override fun searchFollowerList(userId: Long, searchCondition: String?): List<Follow> {
        val builder = BooleanBuilder()
        builder.and(follow.following.id.eq(userId))

        if (!searchCondition.isNullOrBlank()) {
            builder.and(follow.follower.nickname.like("$searchCondition%"))
        }

        return queryFactory.select(follow)
                .from(follow)
                .where(builder)
                .fetch()
    }
}