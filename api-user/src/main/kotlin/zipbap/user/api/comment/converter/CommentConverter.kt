package zipbap.user.api.comment.converter

import zipbap.user.api.comment.dto.CommentRequestDto
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

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
