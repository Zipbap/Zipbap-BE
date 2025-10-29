package zipbap.user.api.comment.servise

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.comment.converter.CommentConverter
import zipbap.user.api.comment.dto.CommentRequestDto
import zipbap.user.api.comment.dto.CommentResponseDto
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.comment.CommentRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.User
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.code.status.ErrorStatus

@Service
class CommentService(
        private val commentRepository: CommentRepository,
        private val recipeRepository: RecipeRepository
) {

    /**
     * 댓글 생성 (루트 댓글 또는 대댓글)
     */
    @Transactional
    fun createComment(user: User, dto: CommentRequestDto.CreateCommentRequestDto): CommentResponseDto.CommentDetailResponseDto {
        val recipe = recipeRepository.findById(dto.recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        val parent: Comment? = dto.parentId?.let {
            commentRepository.findById(it)
                .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }
        }

        val comment = commentRepository.save(CommentConverter.toEntity(dto, user, recipe, parent))
        return CommentResponseDto.CommentDetailResponseDto.from(comment)
    }

    /**
     * 특정 레시피의 모든 댓글/대댓글 조회
     */
    @Transactional(readOnly = true)
    fun getComments(recipeId: String): List<CommentResponseDto.CommentDetailResponseDto> {
        recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        val rootComments = commentRepository.findAllByRecipeIdAndParentIsNull(recipeId)
        return rootComments.map { mapToDtoWithChildren(it) }
    }

    private fun mapToDtoWithChildren(comment: Comment): CommentResponseDto.CommentDetailResponseDto {
        val childrenDtos = comment.children.map { mapToDtoWithChildren(it) }
        return CommentResponseDto.CommentDetailResponseDto.from(comment, childrenDtos)
    }

    /**
     * 댓글 수정
     */
    @Transactional
    fun updateComment(commentId: Long, dto: CommentRequestDto.UpdateCommentRequestDto, user: User) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        if (comment.user.id != user.id) throw GeneralException(ErrorStatus.COMMENT_FORBIDDEN)
        comment.content = dto.content
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    fun deleteComment(commentId: Long, user: User) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        if (comment.user.id != user.id) throw GeneralException(ErrorStatus.COMMENT_FORBIDDEN)
        commentRepository.delete(comment)
    }
}
