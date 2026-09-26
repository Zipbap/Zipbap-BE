package zipbap.user.api.file.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional
class FileService(private val fileRepository: FileRepository) {
    private fun ownedFiles(urls: Set<String>, userId: Long): List<FileEntity> = urls.map { url ->
        val file = fileRepository.findByFileUrl(url) ?: throw GeneralException(ErrorStatus.BAD_REQUEST)
        if (file.uploader?.id != userId) throw GeneralException(ErrorStatus.FORBIDDEN)
        file
    }

    fun updateRecipeFileStatuses(recipeId: String, usedFileUrls: Set<String>, targetStatus: FileStatus, recipe: Recipe) {
        // Validate every reference before changing attachments; never infer ownership from client input.
        val usedFiles = ownedFiles(usedFileUrls, recipe.user.id!!)
        if (usedFiles.any { it.recipe != null && it.recipe?.id != recipeId }) {
            throw GeneralException(ErrorStatus.FORBIDDEN)
        }
        fileRepository.findAllByRecipeId(recipeId).filter { it.fileUrl !in usedFileUrls }.forEach {
            it.recipe = null
            it.status = if (it.user == null) FileStatus.UNTRACKED else FileStatus.FINALIZED
        }
        usedFiles.forEach {
            it.recipe = recipe
            it.status = if (it.user == null) targetStatus else FileStatus.FINALIZED
        }
    }

    fun deleteFileStatuses(recipeId: String, recipe: Recipe) {
        fileRepository.findAllByRecipeId(recipeId).forEach {
            it.recipe = null
            it.status = if (it.user == null) FileStatus.UNTRACKED else FileStatus.FINALIZED
        }
    }

    fun updateUserFileStatuses(usedFileUrls: Set<String>, targetStatus: FileStatus, managedUser: User) {
        val usedFiles = ownedFiles(usedFileUrls, managedUser.id!!)
        fileRepository.findAllByUser(managedUser).filter { it.fileUrl !in usedFileUrls }.forEach {
            it.user = null
            it.status = if (it.recipe == null) FileStatus.UNTRACKED else it.status
        }
        usedFiles.forEach {
            it.user = managedUser
            it.status = targetStatus
        }
    }
}
