package zipbap.global.domain.file

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.user.User

interface FileRepository : JpaRepository<FileEntity, Long> {
    fun findByFileUrl(fileUrl: String): FileEntity?

    // Recipe ID 기준으로 파일 목록 조회
    fun findAllByRecipeId(recipeId: String): List<FileEntity>

    // 썸네일 등록에 따른 요구사항 추가로, User로 조회하는 기능 추가
    fun findAllByUser(user: User): List<FileEntity>
}
