package zipbap.app.api.comment.dto

import io.swagger.v3.oas.annotations.media.Schema
import zipbap.app.domain.comment.Comment
import java.time.LocalDateTime

/**
 * 댓글 응답 DTO 모음
 */
object CommentResponseDto {

    /**
     * 댓글 상세 응답 DTO
     */
    data class CommentDetailResponseDto(
        @Schema(description = "댓글 ID", example = "1")
        val id: Long,

        @Schema(description = "작성자 닉네임", example = "Sunny")
        val nickname: String,

        @Schema(description = "작성자 프로필 이미지", example = "https://cdn.zipbap.store/profile.jpg")
        val profileImage: String?,

        @Schema(description = "댓글 내용", example = "정말 맛있어 보여요!")
        val content: String,

        @Schema(description = "작성 시각")
        val createdAt: LocalDateTime,

        @Schema(description = "수정 시각")
        val updatedAt: LocalDateTime, 

        @Schema(description = "대댓글 리스트")
        val children: List<CommentDetailResponseDto> = emptyList()
    ) {
        companion object {
            fun from(
                comment: Comment,
                children: List<CommentDetailResponseDto> = emptyList()
            ): CommentDetailResponseDto =
                CommentDetailResponseDto(
                    id = comment.id!!,
                    nickname = comment.user.nickname,
                    profileImage = comment.user.profileImage,
                    content = comment.content,
                    createdAt = comment.createdAt!!,
                    updatedAt = comment.updatedAt!!,
                    children = children
                )
        }
    }
}
