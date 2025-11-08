package zipbap.user.api.feed.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.global.domain.bookmark.BookmarkRepository
import zipbap.global.domain.cookingorder.CookingOrderRepository
import zipbap.user.api.feed.converter.FeedConverter
import zipbap.user.api.feed.dto.FeedResponseDto
import zipbap.global.domain.feed.FeedFilterType
import zipbap.global.domain.feed.FeedQueryRepository
import zipbap.global.domain.like.RecipeLikeRepository
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
    private val cookingOrderRepository: CookingOrderRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val recipeLikeRepository: RecipeLikeRepository
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

            if (loginUser != null && row.recipeId != null) {
                val recipe = recipeRepository.findById(row.recipeId).orElse(null)
                if (recipe != null) {
                    row.isLiked = recipeLikeRepository.existsByUserAndRecipe(loginUser, recipe)
                    row.isBookmarked = bookmarkRepository.existsByUserAndRecipe(loginUser, recipe)
                }
            }

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

        val recipe = recipeRepository.findById(recipeId).orElseThrow {
            GeneralException(ErrorStatus.RECIPE_NOT_FOUND)
        }

        recipe.addViewCount()
        val orders = cookingOrderRepository.findAllByRecipeId(recipeId)

        if (loginUser != null) {
            row.isLiked = recipeLikeRepository.existsByUserAndRecipe(loginUser, recipe)
            row.isBookmarked = bookmarkRepository.existsByUserAndRecipe(loginUser, recipe)
        }

        row.isOwner = (loginUser?.id == recipe.user.id)

        return FeedConverter.toFeedDetailDto(row, orders)
    }
}