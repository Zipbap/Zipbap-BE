package zipbap.app.domain.file

import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<FileEntity, Long> {
    fun findByFileUrl(fileUrl: String): FileEntity?

    // Recipe ID 기준으로 파일 목록 조회
    fun findAllByRecipeId(recipeId: String): List<FileEntity>
}
