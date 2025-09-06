package zipbap.app.api.user.dto

class UserResponseDto {

    data class UserProfileDto(
            val id: Long,
            val nickname: String,
            val isPrivate: Boolean,
            val statusMessage: String?,
            val profileImage: String?,
    )
}