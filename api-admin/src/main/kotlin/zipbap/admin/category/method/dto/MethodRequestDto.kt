package zipbap.admin.category.method.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MethodRequestDto {

    @Schema(description = "요리 방법 생성 요청 DTO")
    data class CreateMethodDto(
        @field:NotBlank(message = "요리 방법은 필수 값입니다.")
        @field:Size(max = 50, message = "요리 방법은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 방법 명칭", example = "볶음")
        val method: String
    )

    @Schema(description = "요리 방법 수정 요청 DTO")
    data class UpdateMethodDto(
        @field:Size(max = 50, message = "요리 방법은 최대 50자까지 가능합니다.")
        @Schema(description = "요리 방법 명칭", example = "끓이기")
        val method: String?
    )
}
