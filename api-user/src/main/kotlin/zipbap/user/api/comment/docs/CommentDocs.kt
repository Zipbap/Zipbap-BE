package zipbap.user.api.comment.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.user.api.comment.dto.CommentRequestDto
import zipbap.user.api.comment.dto.CommentResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.global.global.auth.resolver.UserInjection
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "Comment", description = "댓글/대댓글 API")
@RequestMapping("/api/comments")
interface CommentDocs {

    @Operation(summary = "댓글 작성", description = "레시피에 댓글 또는 대댓글을 작성합니다.")
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "댓글 작성 성공",
                content = [Content(schema = Schema(implementation = CommentResponseDto.CommentDetailResponseDto::class))]
            )
        ]
    )
    @PostMapping
    fun createComment(
            @UserInjection userId: Long,
            @RequestBody dto: CommentRequestDto.CreateCommentRequestDto
    ): ApiResponse<CommentResponseDto.CommentDetailResponseDto>

    @Operation(summary = "댓글 목록 조회", description = "레시피에 달린 모든 댓글과 대댓글을 조회합니다.")
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "댓글 목록 조회 성공",
                content = [Content(schema = Schema(implementation = CommentResponseDto.CommentDetailResponseDto::class))]
            )
        ]
    )
    @GetMapping
    fun getComments(
        @RequestParam recipeId: String
    ): ApiResponse<List<CommentResponseDto.CommentDetailResponseDto>>

    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글 내용을 수정합니다.")
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "댓글 수정 성공")
        ]
    )
    @PutMapping("/{commentId}")
    fun updateComment(
            @UserInjection userId: Long,
            @PathVariable commentId: Long,
            @RequestBody dto: CommentRequestDto.UpdateCommentRequestDto
    ): ApiResponse<Unit>

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다.")
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "댓글 삭제 성공")
        ]
    )
    @DeleteMapping("/{commentId}")
    fun deleteComment(
            @UserInjection userId: Long,
            @PathVariable commentId: Long
    ): ApiResponse<Unit>
}
