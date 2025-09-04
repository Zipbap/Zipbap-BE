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

    override fun createRecipe(
        dto: RecipeRequestDto.CreateRecipeRequest,
        userId: Long
    ): ApiResponse<RecipeResponseDto.CreateRecipeResponse> =
        ApiResponse.onSuccess(recipeService.createRecipe(dto, userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정
}
