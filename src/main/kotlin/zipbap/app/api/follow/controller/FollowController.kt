package zipbap.app.api.follow.controller

import jakarta.annotation.Nullable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.follow.docs.FollowDocs
import zipbap.app.api.follow.dto.FollowRequestDto
import zipbap.app.api.follow.dto.FollowResponseDto
import zipbap.app.api.follow.service.FollowService
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
class FollowController(
        private val followService: FollowService
) : FollowDocs {

    override fun followUser(user:User, followingId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
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