package zipbap.admin.category.level.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class LevelRequestDto {

    @Schema(description = "난이도 생성 요청 DTO")
    data class CreateLevelDto(
        @field:NotBlank(message = "난이도는 필수 값입니다.")
        @field:Size(max = 50, message = "난이도는 최대 50자까지 가능합니다.")
        @Schema(description = "난이도 명칭", example = "초급")
        val level: String
    )

    @Schema(description = "난이도 수정 요청 DTO")
    data class UpdateLevelDto(
        @field:Size(max = 50, message = "난이도는 최대 50자까지 가능합니다.")
        @Schema(description = "난이도 명칭", example = "고급")
        val level: String?
    )
}
