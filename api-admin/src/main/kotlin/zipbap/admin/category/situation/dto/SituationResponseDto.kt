package zipbap.admin.category.situation.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요리 상황 카테고리 응답 DTO")
data class SituationResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "요리 상황 명칭", example = "일상")
    val situation: String
)
