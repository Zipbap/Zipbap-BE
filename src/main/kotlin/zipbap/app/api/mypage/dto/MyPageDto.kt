package zipbap.app.api.mypage.dto

import java.time.LocalDateTime

data class ProfileBlock(
        val id: Long,
        val nickname: String,
        val profileImage: String?,
        val followers: Long,
        val followings: Long,
        val isFollowing: Boolean, // viewer -> owner
)

data class RecipeCardDto(
        val id: String,               // Recipe id가 String
        val title: String?,
        val subtitle: String?,
        val thumbnail: String?,
        val createdAt: LocalDateTime
)