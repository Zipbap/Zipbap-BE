package zipbap.user.api.feed.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.global.domain.cookingorder.CookingOrderRepository
import zipbap.user.api.feed.converter.FeedConverter
import zipbap.user.api.feed.dto.FeedResponseDto
import zipbap.global.domain.feed.FeedFilterType
import zipbap.global.domain.feed.FeedQueryRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.User
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

/**
 * FeedService
 *
 * - 피드 조회 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
class FeedService(
    private val feedQueryRepository: FeedQueryRepository,
    private val recipeRepository: RecipeRepository,
        private val cookingOrderRepository: CookingOrderRepository
) {

    fun getFeedList(
            loginUser: User?,
            filter: FeedFilterType,
            pageable: Pageable,
            condition: String?
    ): Page<FeedResponseDto.FeedItemResponseDto> {
        if (filter == FeedFilterType.FOLLOWING && loginUser == null) {
            throw GeneralException(ErrorStatus.UNAUTHORIZED)
        }

        val page = feedQueryRepository.findFeed(loginUser, filter, pageable, condition)

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

        recipeRepository.findById(recipeId).get().addViewCount()
        val orders = cookingOrderRepository.findAllByRecipeId(recipeId)

        row.isLiked = false // TODO: 실제 구현 필요
        row.isBookmarked = false

        return FeedConverter.toFeedDetailDto(row, orders)
    }
}
