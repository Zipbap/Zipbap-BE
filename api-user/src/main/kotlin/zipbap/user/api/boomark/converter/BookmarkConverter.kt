package zipbap.user.api.boomark.converter

import zipbap.user.api.boomark.dto.BookmarkResponseDto
import zipbap.global.domain.recipe.Recipe

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