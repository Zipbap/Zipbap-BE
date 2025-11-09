package zipbap.global.domain.mypage

import java.time.LocalDateTime

object MyPageQueryResult {
    data class ProfileBlock(
            val id: Long,
            val nickname: String,
            val profileImage: String?,
            val followers: Long,
            val followings: Long,
            val isFollowing: Boolean, // viewer -> owner
            val statusMessage: String?
    )

    data class RecipeCard(
            val id: String,               // Recipe id가 String
            val title: String?,
            val subtitle: String?,
            val thumbnail: String?,
            val createdAt: LocalDateTime
    )
}
