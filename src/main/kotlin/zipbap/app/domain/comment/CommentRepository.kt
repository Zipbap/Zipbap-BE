package zipbap.app.domain.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByRecipeIdAndParentIsNull(recipeId: String): List<Comment>
    fun findAllByParentId(parentId: Long): List<Comment>
}
