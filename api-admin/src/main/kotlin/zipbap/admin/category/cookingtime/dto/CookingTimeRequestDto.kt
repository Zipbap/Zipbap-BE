package zipbap.admin.category.cookingtime.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CookingTimeRequestDto {

    @Schema(description = "요리 시간 생성 요청 DTO")
    data class CreateCookingTimeDto(
        @field:NotBlank(message = "요리 시간은 필수 값입니다.")
        @field:Size(max = 50, message = "요리 시간은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 시간 명칭", example = "5분 이내")
        val cookingTime: String
    )

    @Schema(description = "요리 시간 수정 요청 DTO")
    data class UpdateCookingTimeDto(
        @field:Size(max = 50, message = "요리 시간은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 시간 명칭", example = "2시간 이상")
        val cookingTime: String?
    )
}
