package zipbap.app.admin.category.situation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class SituationRequestDto {

    @Schema(description = "요리 상황 카테고리 생성 요청 DTO")
    data class CreateSituationDto(
        @field:NotBlank(message = "요리 상황 카테고리는 필수 값입니다.")
        @field:Size(max = 50, message = "요리 상황 카테고리는 최대 50자까지 가능합니다.")
        @Schema(description = "요리 상황 명칭", example = "다이어트")
        val situation: String
    )

    @Schema(description = "요리 상황 카테고리 수정 요청 DTO")
    data class UpdateSituationDto(
        @field:Size(max = 50, message = "요리 상황 카테고리는 최대 50자까지 가능합니다.")
        @Schema(description = "요리 상황 명칭", example = "일상")
        val situation: String?
    )
}
