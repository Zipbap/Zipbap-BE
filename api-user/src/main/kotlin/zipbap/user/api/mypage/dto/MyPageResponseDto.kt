package zipbap.user.api.mypage.dto

import org.springframework.data.domain.Page
import zipbap.global.domain.mypage.MyPageQueryResult
import java.time.LocalDateTime

object MyPageResponseDto {

    data class MyPageViewDto(
            val profileBlockDto: ProfileDto,
            val recipeCardDtoPage: Page<RecipeCardDto>,
            val isOwner: Boolean,
            val isFeed: Boolean
    )

    data class ProfileDto(
            val id: Long,
            val nickname: String,
            val profileImage: String?,
            val followers: Long,
            val followings: Long,
            val isFollowing: Boolean, // viewer -> owner
            val statusMessage: String?
    ) {
        constructor(profileBlock: MyPageQueryResult.ProfileBlock)
                : this(
                        id = profileBlock.id,
                        nickname = profileBlock.nickname,
                        profileImage = profileBlock.profileImage,
                        followers = profileBlock.followers,
                        followings = profileBlock.followings,
                        isFollowing = profileBlock.isFollowing,
                        statusMessage = profileBlock.statusMessage
                )
    }

    data class RecipeCardDto(
            val id: String,               // Recipe id가 String
            val title: String?,
            val subtitle: String?,
            val thumbnail: String?,
            val createdAt: LocalDateTime
    ) {
        constructor(recipeCard: MyPageQueryResult.RecipeCard)
                : this(
                        id = recipeCard.id,
                        title = recipeCard.title,
                        subtitle = recipeCard.subtitle,
                        thumbnail = recipeCard.thumbnail,
                        createdAt = recipeCard.createdAt
                )
    }

}