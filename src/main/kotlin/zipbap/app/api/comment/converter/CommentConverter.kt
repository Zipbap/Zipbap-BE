package zipbap.app.api.comment.converter

import zipbap.app.api.comment.dto.CommentRequestDto
import zipbap.app.domain.comment.Comment
import zipbap.app.domain.recipe.Recipe
import zipbap.app.domain.user.User

/**
 * 댓글 엔티티 변환기
 */
object CommentConverter {

    /**
     * 댓글 생성 요청 DTO → 엔티티 변환
     */
    fun toEntity(
        dto: CommentRequestDto.CreateCommentRequestDto,
        user: User,
        recipe: Recipe,
        parent: Comment? = null
    ): Comment = Comment(
        user = user,
        recipe = recipe,
        content = dto.content,
        parent = parent
    )
}
