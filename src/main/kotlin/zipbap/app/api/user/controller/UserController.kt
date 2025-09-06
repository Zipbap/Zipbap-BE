package zipbap.app.api.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.follow.dto.FollowResponseDto
import zipbap.app.api.user.docs.UserDocs
import zipbap.app.api.user.dto.UserRequestDto
import zipbap.app.api.user.dto.UserResponseDto
import zipbap.app.api.user.service.UserService
import zipbap.app.domain.user.User
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

}