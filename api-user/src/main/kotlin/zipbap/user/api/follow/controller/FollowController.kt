package zipbap.user.api.follow.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.follow.docs.FollowDocs
import zipbap.user.api.follow.dto.FollowResponseDto
import zipbap.user.api.follow.service.FollowService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
class FollowController(
        private val followService: FollowService
) : FollowDocs {

    override fun followUser(user: User, followingId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.follow(user, followingId))
    }

    override fun unfollowUser(user: User, unfollowingId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.unfollow(user, unfollowingId))
    }

    override fun countFollow(user: User, userId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.count(user, userId))
    }

    override fun getFollowingList(user: User, userId: Long,
                                  condition: String?): ApiResponse<List<FollowResponseDto.FollowUserDto>> {
        return ApiResponse.onSuccess(followService.followingList(user, userId, condition))
    }

    override fun getFollowerList(user: User, userId: Long,
                                 condition: String?): ApiResponse<List<FollowResponseDto.FollowUserDto>> {
        return ApiResponse.onSuccess(followService.followerList(user, userId, condition))
    }
}