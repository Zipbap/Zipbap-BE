package zipbap.user.api.comment.controller

import org.springframework.web.bind.annotation.*
import zipbap.user.api.comment.docs.CommentDocs
import zipbap.user.api.comment.dto.CommentRequestDto
import zipbap.user.api.comment.dto.CommentResponseDto
import zipbap.user.api.comment.servise.CommentService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.global.global.auth.resolver.UserInjection

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService
) : CommentDocs {

    override fun createComment(
            @UserInjection userId: Long,
            @RequestBody dto: CommentRequestDto.CreateCommentRequestDto
    ): ApiResponse<CommentResponseDto.CommentDetailResponseDto> =
        ApiResponse.onSuccess(commentService.createComment(userId, dto))

    override fun getComments(
        @RequestParam recipeId: String
    ): ApiResponse<List<CommentResponseDto.CommentDetailResponseDto>> =
        ApiResponse.onSuccess(commentService.getComments(recipeId))

    override fun updateComment(
            @UserInjection userId: Long,
            @PathVariable commentId: Long,
            @RequestBody dto: CommentRequestDto.UpdateCommentRequestDto
    ): ApiResponse<Unit> {
        commentService.updateComment(commentId, dto, userId)
        return ApiResponse.onSuccess(Unit)
    }

    override fun deleteComment(
            @UserInjection userId: Long,
            @PathVariable commentId: Long
    ): ApiResponse<Unit> {
        commentService.deleteComment(commentId, userId)
        return ApiResponse.onSuccess(Unit)
    }
}
