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
import zipbap.app.api.follow.dto.FollowRequestDto
import zipbap.app.api.follow.dto.FollowResponseDto
import zipbap.app.api.follow.service.FollowService
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/follows")
class FollowController(
        private val followService: FollowService
) {

    @PostMapping("/{followingId}")
    fun followUser(@UserInjection user:User, @PathVariable followingId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.follow(user, followingId))
    }

    @DeleteMapping("/{unfollowingId}")
    fun unfollowUser(@UserInjection user: User, @PathVariable unfollowingId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.unfollow(user, unfollowingId))
    }

    @GetMapping("/{userId}/count")
    fun countFollow(@UserInjection user: User, @PathVariable userId: Long): ApiResponse<FollowResponseDto.FollowCountDto> {
        return ApiResponse.onSuccess(followService.count(user, userId))
    }

    @GetMapping("/{userId}/following-list")
    fun getFollowingList(@UserInjection user: User, @PathVariable userId: Long,
                         @RequestParam(required = false) condition: String?): ApiResponse<List<FollowResponseDto.FollowUserDto>> {
        return ApiResponse.onSuccess(followService.followingList(user, userId, condition))
    }

    @GetMapping("/{userId}/follower-list")
    fun getFollowerList(@UserInjection user: User, @PathVariable userId: Long,
                        @RequestParam(required = false) condition: String?): ApiResponse<List<FollowResponseDto.FollowUserDto>> {
        return ApiResponse.onSuccess(followService.followerList(user, userId, condition))
    }
}