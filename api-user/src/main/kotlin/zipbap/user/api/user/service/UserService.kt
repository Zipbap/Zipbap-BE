package zipbap.user.api.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.user.converter.UserConverter
import zipbap.user.api.user.dto.UserRequestDto
import zipbap.user.api.user.dto.UserResponseDto
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.user.SocialType
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.user.api.file.service.FileService
import zipbap.global.domain.user.findByIdOrThrow
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val fileService: FileService,
) {

    fun isUserExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    /*
        이제 register가 id를 반환할 수 있도록 변경하였습니다. - 2026.03.11
        기존 계정과 소셜 제공자가 다르면 이메일만으로 계정을 연결하지 않습니다.
        나중에 관련 기획이 추가되면 변경 고려해야합니ㅏㄷ.
     */
    @Transactional
    fun register(registrationId: String, username: String, email: String): Long {
        val social = SocialType.valueOf(registrationId.uppercase())
        return userRepository.findByEmail(email)?.let {
            if (it.socialType != social) throw GeneralException(ErrorStatus.OAUTH2_LOGIN_FAIL)
            it.id!!
        } ?: run {
            val newUser = User(
                email = email,
                nickname = username,
                socialType = social
            )
            userRepository.save(newUser).id!!
        }

    }

    fun getUserProfile(userId: Long): UserResponseDto.UserProfileDto {
        val user = userRepository.findByIdOrThrow(userId)
        return UserConverter.toProfileDto(user)
    }

    @Transactional
    fun updateUserProfile(
            userId: Long,
        dto: UserRequestDto.UserUpdateDto
    ): UserResponseDto.UserProfileDto {

        // ✅ 반드시 영속 상태 User 로 다시 조회
        val managedUser = userRepository.findByIdOrThrow(userId)

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()
        dto.profileImage?.let { usedFileUrls.add(it) }

        // ✅ 파일 상태 업데이트는 영속 상태 유저로 처리
            fileService.updateUserFileStatuses(usedFileUrls, FileStatus.FINALIZED, managedUser)

        managedUser.update(
            nickname = dto.nickname,
            isPrivate = dto.isPrivate,
            profileImage = dto.profileImage,
            statusMessage = dto.statusMessage
        )

        return UserConverter.toProfileDto(managedUser)
    }


    @Transactional
    fun deleteUser(userId: Long) {

        /**
         * ✅ 삭제 시에도 항상 영속 엔티티 사용해야 함
         */
        val managedUser = userRepository.findByIdOrThrow(userId)

        // 소셜 타입별 후처리 (카카오 unlink / 애플 revoke 등 가능)
        when (managedUser.socialType) {
            SocialType.KAKAO -> { /* TODO: 카카오 unlink 예정 */ }
            SocialType.APPLE -> { /* TODO: 애플 revoke 예정 */ }
        }

        // ✅ CascadeType.REMOVE + orphanRemoval에 의해 연관 객체 자동 삭제
        userRepository.delete(managedUser)
    }

}
