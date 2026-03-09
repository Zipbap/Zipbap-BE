package zipbap.user.api.feed.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.feed.docs.FeedDocs
import zipbap.user.api.feed.dto.FeedRequestDto
import zipbap.user.api.feed.dto.FeedResponseDto
import zipbap.user.api.feed.service.FeedService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.global.domain.feed.FeedFilterType

/**
 * FeedController
 *
 * - 피드 API Controller
 */
@RestController
class FeedController(
    private val feedService: FeedService
) : FeedDocs {

    override fun getFeed(
            userId: Long,
            filter: FeedFilterType?,
            pageable: Pageable,
            condition: String?
    ): ApiResponse<Page<FeedResponseDto.FeedItemResponseDto>> =
        ApiResponse.onSuccess(
            feedService.getFeedList(userId, filter ?: FeedFilterType.ALL, pageable, condition)
        )

    override fun getFeedDetail(
            userId: Long,
            recipeId: String
    ): ApiResponse<FeedResponseDto.FeedDetailResponseDto> =
        ApiResponse.onSuccess(
            feedService.getFeedDetail(userId, recipeId)
        )
}
