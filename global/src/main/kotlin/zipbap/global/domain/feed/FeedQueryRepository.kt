package zipbap.global.domain.feed

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zipbap.global.domain.user.User

/**
 * FeedQueryRepository
 *
 * - 피드 목록/상세 조회를 위한 QueryDSL 기반 Repository
 */
interface FeedQueryRepository {
    fun findFeed(
            loginUser: User?,
            filter: FeedFilterType,
            pageable: Pageable,
            keyword: String?
    ): Page<FeedQueryResult.FeedListRow>

    fun findFeedDetail(
            loginUser: User?,
            recipeId: String
    ): FeedQueryResult.FeedDetailRow?
}
