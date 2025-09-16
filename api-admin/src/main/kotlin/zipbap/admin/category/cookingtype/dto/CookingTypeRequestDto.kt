package zipbap.admin.category.cookingtype.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CookingTypeRequestDto {

    @Schema(description = "요리 유형 생성 요청 DTO")
    data class CreateCookingTypeDto(
        @field:NotBlank(message = "요리 유형은 필수 값입니다.")
        @field:Size(max = 50, message = "요리 유형은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 유형 이름", example = "한식")
        val type: String
    )

    @Schema(description = "요리 유형 수정 요청 DTO")
    data class UpdateCookingTypeDto(
        @field:Size(max = 50, message = "요리 유형은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 유형 이름", example = "퓨전 한식")
        val type: String?
    )
}
