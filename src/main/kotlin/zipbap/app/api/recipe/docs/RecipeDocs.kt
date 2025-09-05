package zipbap.app.api.recipe.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RequestMapping("/api/recipes")
@Tag(name = "Recipe", description = "레시피 관련 API")
interface RecipeDocs {

    @Operation(summary = "임시 레시피 생성", description = "사용자가 임시 저장용 레시피를 생성합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "임시 레시피 생성 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.TempRecipeDetailResponseDto::class))]
        )
    )
    @PostMapping("/temp")
    fun createTempRecipe(
        @UserInjection user: User
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto>

    @Operation(summary = "임시 레시피 업데이트", description = "임시 저장된 레시피를 수정합니다. 최종 저장은 /{recipeId}/finalize 사용.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "임시 레시피 업데이트 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.TempRecipeDetailResponseDto::class))]
        )
    )
    @PutMapping("/{recipeId}/temp")
    fun updateTempRecipe(
        @PathVariable("recipeId") recipeId: String,
        @UserInjection user: User,
        @RequestBody request: RecipeRequestDto.UpdateTempRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.TempRecipeDetailResponseDto>

    @Operation(summary = "레시피 작성(최종 저장)", description = "임시 레시피를 최종 저장합니다. 모든 필드가 필수.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "레시피 작성 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.RecipeDetailResponseDto::class))]
        )
    )
    @PutMapping("/{recipeId}/finalize")
    fun finalizeRecipe(
        @PathVariable("recipeId") recipeId: String,
        @UserInjection user: User,
        @RequestBody request: RecipeRequestDto.FinalizeRecipeRequestDto
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto>

    @Operation(summary = "내 임시 레시피 전체 조회", description = "사용자의 모든 임시 저장 레시피를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.TempRecipeDetailResponseDto::class))]
        )
    )
    @GetMapping("/temp")
    fun getMyTempRecipes(
        @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.TempRecipeDetailResponseDto>>

    @Operation(summary = "내 완성된 레시피 전체 조회", description = "사용자의 모든 완성 레시피를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.RecipeDetailResponseDto::class))]
        )
    )
    @GetMapping
    fun getMyRecipes(
        @UserInjection user: User
    ): ApiResponse<List<RecipeResponseDto.RecipeDetailResponseDto>>
}
