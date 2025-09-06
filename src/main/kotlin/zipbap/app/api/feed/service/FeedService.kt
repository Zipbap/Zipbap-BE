package zipbap.app.api.feed.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.feed.converter.FeedConverter
import zipbap.app.api.feed.dto.FeedRequestDto
import zipbap.app.api.feed.dto.FeedResponseDto
import zipbap.app.domain.feed.FeedQueryRepository
import zipbap.app.domain.user.User
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

/**
 * FeedService
 *
 * - 피드 조회 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
class FeedService(
    private val feedQueryRepository: FeedQueryRepository
) {

    fun getFeedList(
        loginUser: User?,
        filter: FeedRequestDto.FeedFilterType,
        pageable: Pageable
    ): Page<FeedResponseDto.FeedItemResponseDto> {
        if (filter == FeedRequestDto.FeedFilterType.FOLLOWING && loginUser == null) {
            throw GeneralException(ErrorStatus.UNAUTHORIZED)
        }

        val page = feedQueryRepository.findFeed(loginUser, filter, pageable)

        val content = page.content.map { row ->
            // TODO: 실제 로그인 사용자의 좋아요/북마크 여부 조회 로직 필요
            row.isLiked = false
            row.isBookmarked = false
            FeedConverter.toFeedItemDto(row)
        }

        return PageImpl(content, pageable, page.totalElements)
    }

    fun getFeedDetail(
        loginUser: User?,
        recipeId: String
    ): FeedResponseDto.FeedDetailResponseDto {
        val row = feedQueryRepository.findFeedDetail(loginUser, recipeId)
            ?: throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)

        row.isLiked = false // TODO: 실제 구현 필요
        row.isBookmarked = false

        return FeedConverter.toFeedDetailDto(row)
    }
}
