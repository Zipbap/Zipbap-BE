package zipbap.global.domain.feed

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

object FeedQueryResult {

    data class FeedListRow @QueryProjection constructor(
        val nickname: String? = null,
        val profileImage: String? = null,
        val userIsPrivate: Boolean = false,
        val recipeId: String? = null,
        val title: String? = null,
        val thumbnail: String? = null,
        val introduction: String? = null,
        val cookingTime: String? = null,
        val level: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val likeCount: Long = 0,
        val bookmarkCount: Long = 0,
        val commentCount: Long = 0,
        var isLiked: Boolean = false,
        var isBookmarked: Boolean = false,
        var isPrivate: Boolean = false,
        var viewCount: Long = 0
    )

    data class FeedDetailRow @QueryProjection constructor(
        val nickname: String? = null,
        val profileImage: String? = null,
        val statusMessage: String? = null,
        val isFollowing: Boolean = false,
        val recipeId: String? = null,
        val title: String? = null,
        val subtitle: String? = null,
        val introduction: String? = null,
        val thumbnail: String? = null,
        val video: String? = null,
        val ingredientInfo: String? = null,
        val kick: String? = null,
        val recipeIsPrivate: Boolean = false,
        val recipeStatus: String? = null,
        val cookingType: String? = null,
        val situation: String? = null,
        val mainIngredient: String? = null,
        val method: String? = null,
        val headcount: String? = null,
        val cookingTime: String? = null,
        val level: String? = null,
        val myCategory: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val likeCount: Long = 0,
        val bookmarkCount: Long = 0,
        val commentCount: Long = 0,
        var isLiked: Boolean = false,
        var isBookmarked: Boolean = false,
        var viewCount: Long = 0
    )
}
