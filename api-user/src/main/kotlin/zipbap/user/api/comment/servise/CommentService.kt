package zipbap.user.api.comment.servise

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.comment.converter.CommentConverter
import zipbap.user.api.comment.dto.CommentRequestDto
import zipbap.user.api.comment.dto.CommentResponseDto
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.comment.CommentRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.code.status.ErrorStatus

@Service
class CommentService(
        private val commentRepository: CommentRepository,
        private val recipeRepository: RecipeRepository,
        private val userRepository: UserRepository
) {

    /**
     * 댓글 생성 (루트 댓글 또는 대댓글)
     */
    @Transactional
    fun createComment(userId: Long, dto: CommentRequestDto.CreateCommentRequestDto): CommentResponseDto.CommentDetailResponseDto {
        val recipe = recipeRepository.findById(dto.recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }
        val userRef = userRepository.getReferenceById(userId)

        val parent: Comment? = dto.parentId?.let {
            commentRepository.findById(it)
                .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }
        }

        val comment = commentRepository.save(CommentConverter.toEntity(dto, userRef, recipe, parent))
        return CommentResponseDto.CommentDetailResponseDto.from(comment)
    }

    /**
     * 특정 레시피의 모든 댓글/대댓글 조회
     * 2026-1-12 댓글 + 대댓글 개수만큼 추가적인 쿼리가 발생합니다. 댓글 기능 사용할꺼라면 DB단에서 recursive 쿼리로 개선이 필요합니다.
     * DTO 변환 과에서 User 사용하므로 user fetch join도 필요합니다.
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
    fun updateComment(commentId: Long, dto: CommentRequestDto.UpdateCommentRequestDto, userId: Long) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        if (comment.user.id != userId) throw GeneralException(ErrorStatus.COMMENT_FORBIDDEN)
        comment.content = dto.content
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    fun deleteComment(commentId: Long, userId: Long) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        if (comment.user.id != userId) throw GeneralException(ErrorStatus.COMMENT_FORBIDDEN)
        commentRepository.delete(comment)
    }
}
