package zipbap.admin.category.cookingtime.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요리 시간 응답 DTO")
data class CookingTimeResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "요리 시간 명칭", example = "30분 이내")
    val cookingTime: String
)
