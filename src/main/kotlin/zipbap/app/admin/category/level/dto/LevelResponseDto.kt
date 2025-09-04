package zipbap.app.admin.category.level.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "난이도 응답 DTO")
data class LevelResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "난이도 명칭", example = "중급")
    val level: String
)
