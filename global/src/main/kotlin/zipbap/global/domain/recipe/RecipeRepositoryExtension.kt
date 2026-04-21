package zipbap.global.domain.recipe

import org.springframework.data.repository.findByIdOrNull
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

fun RecipeRepository.findByIdOrThrow(recipeId: String): Recipe {
    return this.findByIdOrNull(recipeId)
        ?: throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)
}