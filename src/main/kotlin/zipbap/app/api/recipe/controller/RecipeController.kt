package zipbap.app.api.recipe.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.recipe.docs.RecipeDocs
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.recipe.service.RecipeService
import zipbap.app.global.ApiResponse

@RestController
class RecipeController(
    private val recipeService: RecipeService
) : RecipeDocs {

    override fun createTempRecipe(
        userId: Long
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.createTempRecipe(userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    override fun updateTempRecipe(
        recipeId: String,
        userId: Long,
        request: RecipeRequestDto.UpdateTempRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.updateTempRecipe(request, userId, recipeId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    override fun finalizeRecipe(
        recipeId: String,
        userId: Long,
        request: RecipeRequestDto.finalizeRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto> =
        ApiResponse.onSuccess(recipeService.finalizeRecipe(recipeId, userId, request))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    override fun getMyTempRecipes(
        userId: Long
    ): ApiResponse<List<RecipeResponseDto.TempRecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyTempRecipes(userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    override fun getMyRecipes(
        userId: Long
    ): ApiResponse<List<RecipeResponseDto.RecipeDetailResponseDto>> =
        ApiResponse.onSuccess(recipeService.getMyRecipes(userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정
}
