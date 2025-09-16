package zipbap.admin.category.mainingredient.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요리 주재료 응답 DTO")
data class MainIngredientResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "요리 주재료 명칭", example = "닭고기")
    val ingredient: String
)
