package zipbap.global.domain.comment

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.comment.Comment

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByRecipeIdAndParentIsNull(recipeId: String): List<Comment>
    fun findAllByParentId(parentId: Long): List<Comment>
}
