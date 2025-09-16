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
            @UserInjection user: User,
            @RequestBody dto: CommentRequestDto.CreateCommentRequestDto
    ): ApiResponse<CommentResponseDto.CommentDetailResponseDto> =
        ApiResponse.onSuccess(commentService.createComment(user, dto))

    override fun getComments(
        @RequestParam recipeId: String
    ): ApiResponse<List<CommentResponseDto.CommentDetailResponseDto>> =
        ApiResponse.onSuccess(commentService.getComments(recipeId))

    override fun updateComment(
            @UserInjection user: User,
            @PathVariable commentId: Long,
            @RequestBody dto: CommentRequestDto.UpdateCommentRequestDto
    ): ApiResponse<Unit> {
        commentService.updateComment(commentId, dto, user)
        return ApiResponse.onSuccess(Unit)
    }

    override fun deleteComment(
            @UserInjection user: User,
            @PathVariable commentId: Long
    ): ApiResponse<Unit> {
        commentService.deleteComment(commentId, user)
        return ApiResponse.onSuccess(Unit)
    }
}
