package zipbap.app.api.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.user.converter.UserConverter
import zipbap.app.api.user.dto.UserRequestDto
import zipbap.app.api.user.dto.UserResponseDto
import zipbap.app.domain.user.SocialType
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
        private val userRepository: UserRepository
) {

    fun isUserExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    @Transactional
    fun register(registrationId: String, username: String, email: String) {
        if (isUserExists(email)) return

        val Social = SocialType.valueOf(registrationId.uppercase())
        val user = User(
                email = email,
                nickname = username,
                socialType = Social
        )

        userRepository.save(user)
    }

    fun getUserProfile(user: User): UserResponseDto.UserProfileDto {
        return UserConverter.toProfileDto(user)
    }

    @Transactional
    fun updateUserProfile(user: User, dto: UserRequestDto.UserUpdateDto
    ): UserResponseDto.UserProfileDto {
        user.update(
                nickname = dto.nickname,
                isPrivate = dto.isPrivate,
                profileImage = dto.profileImage,
                statusMessage = dto.statusMessage
        )

        userRepository.save(user)

        return UserConverter.toProfileDto(user)
    }
}