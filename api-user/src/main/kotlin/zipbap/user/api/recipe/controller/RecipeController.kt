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

    override fun getRecipeDetail(
        recipeId: String,
        @UserInjection user: User
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.getRecipeDetail(recipeId, user.id!!))

    override fun getMyActiveRecipesFiltered(
            @UserInjection user: User,
            myCategoryIds: List<String>?
    ): ApiResponse<List<RecipeResponseDto.MyRecipeListItemResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyActiveRecipesFiltered(user.id!!, myCategoryIds))
}
