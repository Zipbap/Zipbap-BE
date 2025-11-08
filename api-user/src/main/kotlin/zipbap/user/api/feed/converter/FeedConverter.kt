package zipbap.user.api.feed.converter

import zipbap.global.domain.cookingorder.CookingOrder
import zipbap.user.api.feed.dto.FeedResponseDto
import zipbap.global.domain.feed.FeedQueryResult
import zipbap.user.api.recipe.dto.RecipeResponseDto

/**
 * FeedConverter
 *
 * - Repository 결과를 API 응답 DTO로 변환
 */
object FeedConverter {

    fun toFeedItemDto(row: FeedQueryResult.FeedListRow): FeedResponseDto.FeedItemResponseDto =
        FeedResponseDto.FeedItemResponseDto(
            nickname = row.nickname ?: "",
            profileImage = row.profileImage,
            userIsPrivate = row.userIsPrivate,
            recipeId = row.recipeId ?: "",
            title = row.title,
            thumbnail = row.thumbnail,
            introduction = row.introduction,
            cookingTime = row.cookingTime,
            level = row.level,
            createdAt = row.createdAt ?: java.time.LocalDateTime.now(),
            updatedAt = row.updatedAt ?: java.time.LocalDateTime.now(),
            isLiked = row.isLiked,
            isBookmarked = row.isBookmarked,
            likeCount = row.likeCount,
            bookmarkCount = row.bookmarkCount,
            commentCount = row.commentCount,
                viewCount = row.viewCount
        )

    fun toFeedDetailDto(
        row: FeedQueryResult.FeedDetailRow,
        orders: List<CookingOrder>
    ): FeedResponseDto.FeedDetailResponseDto =
        FeedResponseDto.FeedDetailResponseDto(
            nickname = row.nickname ?: "",
            profileImage = row.profileImage,
            statusMessage = row.statusMessage,
            isFollowing = row.isFollowing,
            recipeId = row.recipeId ?: "",
            title = row.title,
            subtitle = row.subtitle,
            introduction = row.introduction,
            thumbnail = row.thumbnail,
            video = row.video,
            ingredientInfo = row.ingredientInfo,
            kick = row.kick,
            recipeIsPrivate = row.recipeIsPrivate,
            recipeStatus = row.recipeStatus ?: "",
            cookingType = row.cookingType,
            situation = row.situation,
            mainIngredient = row.mainIngredient,
            method = row.method,
            headcount = row.headcount,
            cookingTime = row.cookingTime,
            level = row.level,
            createdAt = row.createdAt ?: java.time.LocalDateTime.now(),
            updatedAt = row.updatedAt ?: java.time.LocalDateTime.now(),
            likeCount = row.likeCount,
            isLiked = row.isLiked,
            bookmarkCount = row.bookmarkCount,
            isBookmarked = row.isBookmarked,
            commentCount = row.commentCount,
            viewCount = row.viewCount,
            followerCount = row.followerCount,
            myCategory = row.myCategory,
            cookingOrders = orders.sortedBy { it.turn }.map {
                RecipeResponseDto.RecipeDetailResponseDto.CookingOrderResponse(
                    turn = it.turn,
                    image = it.image,
                    description = it.description
                )
            },
            isOwner = row.isOwner
        )
}
