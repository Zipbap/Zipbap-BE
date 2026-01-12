package zipbap.user.api.recipe.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.recipe.docs.RecipeDocs
import zipbap.user.api.recipe.dto.RecipeRequestDto
import zipbap.user.api.recipe.dto.RecipeResponseDto
import zipbap.user.api.recipe.service.RecipeService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
class RecipeController(
    private val recipeService: RecipeService
) : RecipeDocs {

    override fun createTempRecipe(
        @UserInjection userId: Long
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.createTempRecipe(userId))

    override fun updateTempRecipe(
            recipeId: String,
            @UserInjection userId: Long,
            request: RecipeRequestDto.UpdateTempRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.updateTempRecipe(request, userId, recipeId))

    override fun finalizeRecipe(
            recipeId: String,
            @UserInjection userId: Long,
            request: RecipeRequestDto.FinalizeRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.finalizeRecipe(recipeId, userId, request))

    override fun getMyTempRecipes(
        @UserInjection userId: Long
    ): ApiResponse<List<RecipeResponseDto.TempRecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyTempRecipes(userId))

    override fun getMyRecipes(
        @UserInjection userId: Long
    ): ApiResponse<List<RecipeResponseDto.RecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyRecipes(userId))

    override fun getRecipeDetail(
        recipeId: String,
        @UserInjection userId: Long
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.getRecipeDetail(recipeId, userId))

    override fun getMyActiveRecipesFiltered(
            @UserInjection userId: Long,
            myCategoryIds: List<String>?
    ): ApiResponse<List<RecipeResponseDto.MyRecipeListItemResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyActiveRecipesFiltered(userId, myCategoryIds))

    override fun deleteRecipe(
            @UserInjection userId: Long,
            recipeId: String): ApiResponse<String> {
        recipeService.deleteRecipe(recipeId, userId)
        return ApiResponse.onSuccess("Delete Success!")
    }
}
