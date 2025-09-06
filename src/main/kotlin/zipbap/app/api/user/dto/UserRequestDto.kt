package zipbap.app.api.user.dto

class UserRequestDto {

    data class UserUpdateDto(
            val nickname: String,
            val isPrivate: Boolean,
            val profileImage: String?,
            val statusMessage: String?
    )
}