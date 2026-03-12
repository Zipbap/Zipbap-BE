package zipbap.user.api.file.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User
import kotlin.collections.forEach

/**
 * AOP를 위해 따로 Service 작성
 */
@Service
@Transactional
class FileService(
        private val fileRepository: FileRepository
) {
    /**
     * 공통 파일 상태 업데이트 유틸
     * - 사용되지 않는 파일 → UNTRACKED
     * - 사용된 파일 → 주어진 targetStatus 로 업데이트
     */
    fun updateRecipeFileStatuses(
        recipeId: String,
        usedFileUrls: Set<String>,
        targetStatus: FileStatus,
        recipe: Recipe
    ) {
        // 기존에 연결된 파일들
        val attachedFiles = fileRepository.findAllByRecipeId(recipeId)

        // 사용 안 된 파일은 UNTRACKED 처리
        attachedFiles.filter { it.fileUrl !in usedFileUrls }.forEach { file: FileEntity ->
            file.status = FileStatus.UNTRACKED
            file.recipe = null
            fileRepository.save(file)
        }

        // 사용된 파일은 targetStatus 로 변경
        usedFileUrls.forEach { url ->
            fileRepository.findByFileUrl(url)?.apply {
                this.recipe = recipe
                this.status = targetStatus
                fileRepository.save(this)
            }
        }
    }

    fun deleteFileStatuses(
            recipeId: String,
            recipe: Recipe
    ) {
        // 기존에 연결된 파일들
        val attachedFiles = fileRepository.findAllByRecipeId(recipeId)

        // 기존에 연결된 파일들 분리
        attachedFiles.forEach { file: FileEntity ->
            file.status = FileStatus.UNTRACKED
            file.recipe = null
            fileRepository.save(file)
        }
    }

    /**
     * 공통 파일 상태 업데이트
     * - 사용 안 된 파일 → UNTRACKED + user = null
     * - 사용된 파일 → targetStatus + user = managedUser
     */
    fun updateUserFileStatuses(
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
}