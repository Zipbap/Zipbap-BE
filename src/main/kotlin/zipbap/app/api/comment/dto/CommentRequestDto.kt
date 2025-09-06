package zipbap.app.api.comment.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 댓글 요청 DTO 모음
 */
object CommentRequestDto {

    /**
     * 댓글 생성 요청 DTO
     */
    data class CreateCommentRequestDto(
        @Schema(description = "레시피 ID", example = "RC-1-00001")
        val recipeId: String,

        @Schema(description = "댓글 내용", example = "정말 맛있어 보여요!")
        val content: String,

        @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "12")
        val parentId: Long? = null
    )

    /**
     * 댓글 수정 요청 DTO
     */
    data class UpdateCommentRequestDto(
        @Schema(description = "댓글 내용", example = "저도 한번 해볼게요!")
        val content: String
    )
}
