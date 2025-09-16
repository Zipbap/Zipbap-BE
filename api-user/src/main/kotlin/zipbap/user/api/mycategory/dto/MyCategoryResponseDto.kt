package zipbap.user.api.mycategory.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "내 카테고리 응답 DTO")
data class MyCategoryResponseDto(
    @Schema(description = "ID", example = "MC-1-00001")
    val id: String,

    @Schema(description = "내 카테고리 명칭", example = "다이어트 요리")
    val name: String,
)
