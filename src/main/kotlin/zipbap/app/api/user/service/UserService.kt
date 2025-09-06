package zipbap.app.api.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.user.converter.UserConverter
import zipbap.app.api.user.dto.UserRequestDto
import zipbap.app.api.user.dto.UserResponseDto
import zipbap.app.domain.file.FileEntity
import zipbap.app.domain.file.FileRepository
import zipbap.app.domain.file.FileStatus
import zipbap.app.domain.user.SocialType
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository

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

    fun getUserProfile(user: User): UserResponseDto.UserProfileDto {
        return UserConverter.toProfileDto(user)
    }

    @Transactional
    fun updateUserProfile(user: User, dto: UserRequestDto.UserUpdateDto
    ): UserResponseDto.UserProfileDto {

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()

        dto.profileImage?.let {
            usedFileUrls.add(it)
        }
        // 파일 상태 업데이트 (최종 저장은 FINALIZED 처리)
        updateFileStatuses(usedFileUrls, FileStatus.FINALIZED, user)

        user.update(
                nickname = dto.nickname,
                isPrivate = dto.isPrivate,
                profileImage = dto.profileImage,
                statusMessage = dto.statusMessage
        )

        userRepository.save(user)

        return UserConverter.toProfileDto(user)
    }


    /**
     * 공통 파일 상태 업데이트 유틸
     * - 사용되지 않는 파일 → UNTRACKED
     * - 사용된 파일 → 주어진 targetStatus 로 업데이트
     */
    private fun updateFileStatuses(
            usedFileUrls: Set<String>,
            targetStatus: FileStatus,
            user: User
    ) {
        // 기존에 연결된 파일들
        val attachedFiles = fileRepository.findAllByUser(user)

        // 사용 안 된 파일은 UNTRACKED 처리
        attachedFiles.filter { it.fileUrl !in usedFileUrls }.forEach { file: FileEntity ->
            file.status = FileStatus.UNTRACKED
            file.user = null
            fileRepository.save(file)
        }

        // 사용된 파일은 targetStatus 로 변경
        usedFileUrls.forEach { url ->
            fileRepository.findByFileUrl(url)?.apply {
                this.user = user
                this.status = targetStatus
                fileRepository.save(this)
            }
        }
    }
}