package zipbap.app.api.boomark.dto

import io.swagger.v3.oas.annotations.media.Schema

data class BookmarkResponseDto(
        @Schema(description = "레시피 ID", example = "RC-1-00001")
        val recipeId: String,

        @Schema(description = "좋아요 수", example = "10")
        val likeCount: Long
)
