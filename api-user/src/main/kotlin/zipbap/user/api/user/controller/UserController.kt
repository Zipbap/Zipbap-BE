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


    override fun getProfile(user: User): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.getUserProfile(user))
    }

    override fun updateProfile(user: User, dto:
    UserRequestDto.UserUpdateDto): ApiResponse<UserResponseDto.UserProfileDto> {
        return ApiResponse.onSuccess(userService.updateUserProfile(user, dto))
    }

    override fun deleteUser(user: User): ApiResponse<String> {
        userService.deleteUser(user)
        return ApiResponse.onSuccess("Delete Success!")
    }

}