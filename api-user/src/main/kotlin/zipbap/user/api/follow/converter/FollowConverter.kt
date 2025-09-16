package zipbap.user.api.follow.converter

import zipbap.user.api.follow.dto.FollowResponseDto
import zipbap.global.domain.user.User

object FollowConverter {

    fun toCountDto(userId: Long, followingCount: Long, followerCount: Long, isFollow: Boolean): FollowResponseDto.FollowCountDto {
        return FollowResponseDto.FollowCountDto(
                userId = userId,
                followingCount = followingCount,
                followerCount = followerCount,
                isFollow = isFollow
        )
    }

    fun toUserDto(user: User, isFollow: Boolean): FollowResponseDto.FollowUserDto {
        return FollowResponseDto.FollowUserDto(
                userId = user.id!!,
                nickname = user.nickname,
                profileImage = user.profileImage,
                statusMessage = user.statusMessage,
                isFollow = isFollow
        )
    }
}