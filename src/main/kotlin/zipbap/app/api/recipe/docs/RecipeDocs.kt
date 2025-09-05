package zipbap.app.api.recipe.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.global.ApiResponse

@RequestMapping("/api/recipes")
interface RecipeDocs {

    @Operation(
        summary = "레시피 작성",
        description = "사용자가 새로운 레시피를 작성합니다. (JWT 적용 전까지는 userId 파라미터 사용)"
    )
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "레시피 작성 성공",
            content = [Content(schema = Schema(implementation = RecipeResponseDto.RecipeDetailResponseDto::class))]
        )
    )
    @PostMapping
    fun registerRecipe(
        @RequestBody request: RecipeRequestDto.RegisterRecipeRequestDto,
        @RequestParam("userId") userId: Long
    ): ApiResponse<RecipeResponseDto.RecipeDetailResponseDto>
}
