package zipbap.app.api.follow.dto

class FollowResponseDto {

    data class FollowCountDto(
            val userId: Long,
            val followingCount: Long,
            val followerCount: Long,
            val isFollow: Boolean = false
    )

    data class FollowUserDto(
            val userId: Long,
            val nickname: String,
            val profileImage: String?,
            val statusMessage: String?,
            val isFollow: Boolean = false
    )
}