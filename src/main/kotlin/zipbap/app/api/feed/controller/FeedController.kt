package zipbap.app.api.feed.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.feed.docs.FeedDocs
import zipbap.app.api.feed.dto.FeedRequestDto
import zipbap.app.api.feed.dto.FeedResponseDto
import zipbap.app.api.feed.service.FeedService
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

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
        user: User?,
        filter: FeedRequestDto.FeedFilterType?,
        pageable: Pageable,
        condition: String?
    ): ApiResponse<Page<FeedResponseDto.FeedItemResponseDto>> =
        ApiResponse.onSuccess(
            feedService.getFeedList(user, filter ?: FeedRequestDto.FeedFilterType.ALL, pageable, condition)
        )

    override fun getFeedDetail(
        user: User?,
        recipeId: String
    ): ApiResponse<FeedResponseDto.FeedDetailResponseDto> =
        ApiResponse.onSuccess(
            feedService.getFeedDetail(user, recipeId)
        )
}
