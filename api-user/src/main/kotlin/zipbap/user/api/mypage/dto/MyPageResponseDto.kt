package zipbap.user.api.mypage.dto

import io.swagger.v3.oas.annotations.media.Schema
import zipbap.global.domain.mypage.MyPageQueryResult
import java.time.LocalDateTime

object MyPageResponseDto {

    data class MyPageViewDto(
        val profileBlockDto: ProfileDto,
        val recipeCardPage: PageResponse<RecipeCardDto>,
        val isOwner: Boolean,
        val isFeed: Boolean
    )

    data class ProfileDto(
        @Schema(description = "유저 ID", example = "4")
        val id: Long,

        @Schema(description = "닉네임", example = "sunny")
        val nickname: String,

        @Schema(description = "프로필 이미지 URL", example = "https://zipbap-bucket.s3.ap-northeast-2.amazonaws.com/profile.png")
        val profileImage: String?,

        @Schema(description = "팔로워 수", example = "124")
        val followers: Long,

        @Schema(description = "팔로잉 수", example = "38")
        val followings: Long,

        @Schema(description = "팔로우 여부", example = "false")
        val isFollowing: Boolean,

        @Schema(description = "상태 메시지", example = "오늘도 잘 부탁해!")
        val statusMessage: String?,

        @Schema(description = "계정 비공개 여부", example = "true")
        val isPrivate: Boolean
    ) {
        constructor(block: MyPageQueryResult.ProfileBlock) : this(
            id = block.id,
            nickname = block.nickname,
            profileImage = block.profileImage,
            followers = block.followers,
            followings = block.followings,
            isFollowing = block.isFollowing,
            statusMessage = block.statusMessage,
            isPrivate = block.isPrivate
        )
    }

    data class RecipeCardDto(
        @Schema(description = "레시피 ID", example = "RC-1-00012")
        val id: String,
        @Schema(description = "제목", example = "간장계란밥")
        val title: String?,
        @Schema(description = "설명", example = "5분 완성 집밥 레시피")
        val subtitle: String?,
        @Schema(description = "썸네일 URL", example = "https://zipbap-bucket.s3.ap-northeast-2.amazonaws.com/thumb.png")
        val thumbnail: String?,
        @Schema(description = "생성 날짜", example = "2025-01-02T12:03:11")
        val createdAt: LocalDateTime
    ) {
        constructor(card: MyPageQueryResult.RecipeCard) : this(
            id = card.id,
            title = card.title,
            subtitle = card.subtitle,
            thumbnail = card.thumbnail,
            createdAt = card.createdAt
        )
    }
}
