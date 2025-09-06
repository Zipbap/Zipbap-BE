package zipbap.app.domain.feed

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zipbap.app.api.feed.dto.FeedRequestDto
import zipbap.app.domain.user.User

/**
 * FeedQueryRepository
 *
 * - 피드 목록/상세 조회를 위한 QueryDSL 기반 Repository
 */
interface FeedQueryRepository {
    fun findFeed(
        loginUser: User?,
        filter: FeedRequestDto.FeedFilterType,
        pageable: Pageable
    ): Page<FeedQueryResult.FeedListRow>

    fun findFeedDetail(
        loginUser: User?,
        recipeId: String
    ): FeedQueryResult.FeedDetailRow?
}
