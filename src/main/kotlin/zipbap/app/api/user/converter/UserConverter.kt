package zipbap.app.api.user.converter

import zipbap.app.api.user.dto.UserResponseDto
import zipbap.app.domain.user.User

object UserConverter {

    fun toProfileDto(user: User): UserResponseDto.UserProfileDto {
        return UserResponseDto.UserProfileDto(
                id = user.id!!,
                isPrivate = user.isPrivate,
                nickname = user.nickname,
                statusMessage = user.statusMessage,
                profileImage = user.profileImage,
        )
    }


}