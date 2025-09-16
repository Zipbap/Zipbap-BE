package zipbap.user.api.follow.dto

import io.swagger.v3.oas.annotations.media.Schema

class FollowResponseDto {

    data class FollowCountDto(
            @Schema(description = "유저 id", example = "5")
            val userId: Long,
            @Schema(description = "팔로잉 수", example = "5")
            val followingCount: Long,
            @Schema(description = "팔로워 수", example = "8")
            val followerCount: Long,
            @Schema(description = "viewer가 해당 유저를 팔로우중인지 여부", example = "false")
            val isFollow: Boolean = false
    )

    data class FollowUserDto(
            @Schema(description = "유저 id", example = "5")
            val userId: Long,
            @Schema(description = "유저 nickname", example = "김선우")
            val nickname: String,
            @Schema(description = "유저 profileImage", example = "http://amazone...")
            val profileImage: String?,
            @Schema(description = "유저 상태 메시지", example = "Fly me to the sky")
            val statusMessage: String?,
            @Schema(description = "viewer가 해당 유저를 팔로우중인지 여부", example = "true")
            val isFollow: Boolean = false
    )
}