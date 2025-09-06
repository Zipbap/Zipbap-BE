package zipbap.app.api.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.user.dto.UserRequestDto
import zipbap.app.api.user.dto.UserResponseDto
import zipbap.app.api.user.service.UserService
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/users")
class UserController(
        private val userService: UserService
) {

    @GetMapping("/profile")
    fun getProfile(@UserInjection user: User): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.getUserProfile(user))
    }

    @PutMapping("/profile")
    fun updateProfile(@UserInjection user: User, dto:
    UserRequestDto.UserUpdateDto): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.updateUserProfile(user, dto))
    }

}