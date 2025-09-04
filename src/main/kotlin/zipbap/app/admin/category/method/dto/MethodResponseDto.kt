package zipbap.app.admin.category.method.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요리 방법 응답 DTO")
data class MethodResponseDto(
    @Schema(description = "ID", example = "1")
    val id: Long,

    @Schema(description = "요리 방법 명칭", example = "볶음")
    val method: String
)
