package zipbap.app.api.recipe.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.recipe.docs.RecipeDocs
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.recipe.service.RecipeService
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
class RecipeController(
    private val recipeService: RecipeService
) : RecipeDocs {

    override fun createTempRecipe(
        @UserInjection user: User
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.createTempRecipe(user.id!!))

    override fun updateTempRecipe(
        recipeId: String,
        @UserInjection user: User,
        request: RecipeRequestDto.UpdateTempRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.updateTempRecipe(request, user.id!!, recipeId))

    override fun finalizeRecipe(
        recipeId: String,
        @UserInjection user: User,
        request: RecipeRequestDto.FinalizeRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.finalizeRecipe(recipeId, user.id!!, request))

    override fun getMyTempRecipes(
        @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.TempRecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyTempRecipes(user.id!!))

    override fun getMyRecipes(
        @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.RecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyRecipes(user.id!!))
}
