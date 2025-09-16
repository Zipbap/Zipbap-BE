package zipbap.admin.category.headcount.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class HeadcountRequestDto {

    @Schema(description = "인분 생성 요청 DTO")
    data class CreateHeadcountDto(
        @field:NotBlank(message = "인분은 필수 값입니다.")
        @field:Size(max = 50, message = "인분은 최대 50자까지 가능합니다.")
        @Schema(description = "인분 명칭", example = "2인분")
        val headcount: String
    )

    @Schema(description = "인분 수정 요청 DTO")
    data class UpdateHeadcountDto(
        @field:Size(max = 50, message = "인분은 최대 50자까지 가능합니다.")
        @Schema(description = "인분 명칭", example = "4인분")
        val headcount: String?
    )
}
