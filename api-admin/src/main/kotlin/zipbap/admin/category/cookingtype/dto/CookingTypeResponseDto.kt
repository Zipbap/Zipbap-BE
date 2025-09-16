package zipbap.admin.category.cookingtype.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요리 유형 응답 DTO")
data class CookingTypeResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "요리 유형 이름", example = "한식")
    val type: String
)
