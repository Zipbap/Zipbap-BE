package zipbap.user.api.like.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import org.springframework.web.bind.annotation.*
import zipbap.user.api.like.dto.LikeResponseDto
import zipbap.app.global.ApiResponse
import zipbap.global.domain.user.User

@RequestMapping("/api/recipes/{recipeId}/likes")
@Tag(name = "Recipe Like", description = "레시피 좋아요 관리 API")
interface LikeDocs {

    @Operation(summary = "레시피 좋아요 추가", description = "특정 레시피에 좋아요를 추가합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "좋아요 추가 성공",
            content = [Content(schema = Schema(implementation = LikeResponseDto::class))]
        )
    )
    @PostMapping
    fun likeRecipe(
            @Parameter(hidden = true) user: User,
            @Parameter(description = "레시피 ID", example = "RC-1-00001")
        @PathVariable recipeId: String
    ): ApiResponse<LikeResponseDto>

    @Operation(summary = "레시피 좋아요 취소", description = "특정 레시피에 누른 좋아요를 취소합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "좋아요 취소 성공",
            content = [Content(schema = Schema(implementation = LikeResponseDto::class))]
        )
    )
    @DeleteMapping
    fun unlikeRecipe(
            @Parameter(hidden = true) user: User,
            @Parameter(description = "레시피 ID", example = "RC-1-00001")
        @PathVariable recipeId: String
    ): ApiResponse<LikeResponseDto>

    @Operation(summary = "레시피 좋아요 개수 조회", description = "특정 레시피의 좋아요 개수를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "좋아요 개수 조회 성공",
            content = [Content(schema = Schema(implementation = LikeResponseDto::class))]
        )
    )
    @GetMapping("/count")
    fun countLikes(
        @Parameter(description = "레시피 ID", example = "RC-1-00001")
        @PathVariable recipeId: String
    ): ApiResponse<LikeResponseDto>
}
