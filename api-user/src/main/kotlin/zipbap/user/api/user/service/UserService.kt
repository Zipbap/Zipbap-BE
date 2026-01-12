package zipbap.user.api.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.user.converter.UserConverter
import zipbap.user.api.user.dto.UserRequestDto
import zipbap.user.api.user.dto.UserResponseDto
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.user.SocialType
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
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

    fun getUserProfile(userId: Long): UserResponseDto.UserProfileDto {
        val user = userRepository.findById(userId)
                .orElseThrow {GeneralException(ErrorStatus.USER_NOT_FOUND)}
        return UserConverter.toProfileDto(user)
    }

    @Transactional
    fun updateUserProfile(
            userId: Long,
        dto: UserRequestDto.UserUpdateDto
    ): UserResponseDto.UserProfileDto {

        // ✅ 반드시 영속 상태 User 로 다시 조회
        val managedUser = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()
        dto.profileImage?.let { usedFileUrls.add(it) }

        // ✅ 파일 상태 업데이트는 영속 상태 유저로 처리
        updateFileStatuses(usedFileUrls, FileStatus.FINALIZED, managedUser)

        managedUser.update(
            nickname = dto.nickname,
            isPrivate = dto.isPrivate,
            profileImage = dto.profileImage,
            statusMessage = dto.statusMessage
        )

        return UserConverter.toProfileDto(managedUser)
    }


    /**
     * 공통 파일 상태 업데이트
     * - 사용 안 된 파일 → UNTRACKED + user = null
     * - 사용된 파일 → targetStatus + user = managedUser
     */
    private fun updateFileStatuses(
        usedFileUrls: Set<String>,
        targetStatus: FileStatus,
        managedUser: User
    ) {
        val attachedFiles = fileRepository.findAllByUser(managedUser)

        // 사용 안된 파일 정리
        attachedFiles.filter { it.fileUrl !in usedFileUrls }.forEach { file ->
            file.status = FileStatus.UNTRACKED
            file.user = null
        }

        // 사용된 파일 상태 처리
        usedFileUrls.forEach { url ->
            fileRepository.findByFileUrl(url)?.apply {
                this.user = managedUser   // ✅ Detached user 대신 managedUser 사용
                this.status = targetStatus
            }
        }
    }


    @Transactional
    fun deleteUser(userId: Long) {

        /**
         * ✅ 삭제 시에도 항상 영속 엔티티 사용해야 함
         */
        val managedUser = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // 소셜 타입별 후처리 (카카오 unlink / 애플 revoke 등 가능)
        when (managedUser.socialType) {
            SocialType.KAKAO -> { /* TODO: 카카오 unlink 예정 */ }
            SocialType.APPLE -> { /* TODO: 애플 revoke 예정 */ }
        }

        // ✅ CascadeType.REMOVE + orphanRemoval에 의해 연관 객체 자동 삭제
        userRepository.delete(managedUser)
    }

}
