package zipbap.user.api.user.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.user.docs.UserDocs
import zipbap.user.api.user.dto.UserRequestDto
import zipbap.user.api.user.dto.UserResponseDto
import zipbap.user.api.user.service.UserService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/users")
class UserController(
        private val userService: UserService
) : UserDocs {


    override fun getProfile(userId: Long): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.getUserProfile(userId))
    }

    override fun updateProfile(userId: Long, dto:
    UserRequestDto.UserUpdateDto): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.updateUserProfile(userId, dto))
    }

    override fun deleteUser(userId: Long): ApiResponse<String> {
        userService.deleteUser(userId)
        return ApiResponse.onSuccess("Delete Success!")
    }

}