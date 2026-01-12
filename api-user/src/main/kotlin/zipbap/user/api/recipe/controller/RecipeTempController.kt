package zipbap.user.api.recipe.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.global.ApiResponse
import zipbap.global.domain.user.User
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.recipe.dto.RecipeResponseDto
import zipbap.user.api.recipe.service.RecipeService

/**
 * 실제 성능 개선을 위해 service 등 리팩터링이 이루어졌으나, 정말 간소화 해도 되는지 상의가 필요한 api를 모아두었습니다.
 */
@RestController
@RequestMapping("/api/temp/recipes")
@Tag(name = "Recipe", description = "레시피 관련 임시 API")
class RecipeTempController(
        private val recipeService: RecipeService
) {
    @GetMapping("/temp")
    fun getMyTempRecipes(
            @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.TempRecipeSummaryResponseDto>> =
            ApiResponse.onSuccess(recipeService.getMyTempRecipesV2(user.id!!))

    @GetMapping
    fun getMyRecipes(
            @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.RecipeSummaryResponseDto>> =
            ApiResponse.onSuccess(recipeService.getMyRecipesV2(user.id!!))


}