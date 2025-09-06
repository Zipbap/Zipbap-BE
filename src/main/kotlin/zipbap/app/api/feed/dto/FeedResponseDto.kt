package zipbap.app.api.feed.dto

import java.time.LocalDateTime

/**
 * FeedResponseDto
 *
 * - 피드 응답 DTO 정의
 */
object FeedResponseDto {

    data class FeedItemResponseDto(
        val nickname: String,
        val profileImage: String?,
        val userIsPrivate: Boolean,
        val recipeId: String,
        val title: String?,
        val thumbnail: String?,
        val introduction: String?,
        val cookingTime: String?,
        val level: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val isLiked: Boolean,
        val isBookmarked: Boolean,
        val likeCount: Long,
        val bookmarkCount: Long,
        val commentCount: Long
    )

    data class FeedDetailResponseDto(
        val nickname: String,
        val profileImage: String?,
        val statusMessage: String?,
        val isFollowing: Boolean,
        val recipeId: String,
        val title: String?,
        val subtitle: String?,
        val introduction: String?,
        val thumbnail: String?,
        val video: String?,
        val ingredientInfo: String?,
        val kick: String?,
        val recipeIsPrivate: Boolean,
        val recipeStatus: String,
        val cookingType: String?,
        val situation: String?,
        val mainIngredient: String?,
        val method: String?,
        val headcount: String?,
        val cookingTime: String?,
        val level: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val likeCount: Long,
        val isLiked: Boolean,
        val bookmarkCount: Long,
        val isBookmarked: Boolean,
        val commentCount: Long
    )
}
