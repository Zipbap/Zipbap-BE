package zipbap.app.api.boomark.converter

import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.domain.recipe.Recipe

object BookmarkConverter {

    fun toSimpleDto(recipeId: String, count: Long): BookmarkResponseDto.BookmarkSimpleResponseDto {
        return BookmarkResponseDto.BookmarkSimpleResponseDto(
                recipeId = recipeId,
                bookmarkCount = count
        )
    }

    fun toThumbnailDto(recipe: Recipe): BookmarkResponseDto.BookmarkRecipeResponseDto {
        return BookmarkResponseDto.BookmarkRecipeResponseDto(
                recipeId = recipe.id,
                thumbnailImage = recipe.thumbnail
                )
    }
}