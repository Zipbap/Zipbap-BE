package zipbap.user.api.user.converter

import zipbap.user.api.user.dto.UserResponseDto
import zipbap.global.domain.user.User

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