package zipbap.user.api.boomark.dto

import io.swagger.v3.oas.annotations.media.Schema

class BookmarkResponseDto() {
        data class BookmarkSimpleResponseDto(
                @Schema(description = "레시피 ID", example = "RC-1-00001")
                val recipeId: String,

                @Schema(description = "북마크 수", example = "10")
                val bookmarkCount: Long
        )

        data class BookmarkRecipeResponseDto(
                @Schema(description = "레시피 ID", example = "RC-1-00001")
                val recipeId: String,

                @Schema(description = "썸네일 이미지", example = "https://naver.com")
                val thumbnailImage: String?
        )
}
