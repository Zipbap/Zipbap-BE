package zipbap.admin.category.mainingredient.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MainIngredientRequestDto {

    @Schema(description = "요리 주재료 생성 요청 DTO")
    data class CreateMainIngredientDto(
        @field:NotBlank(message = "요리 주재료는 필수 값입니다.")
        @field:Size(max = 50, message = "요리 주재료는 최대 50자까지 가능합니다.")
        @Schema(description = "요리 주재료 명칭", example = "닭고기")
        val ingredient: String
    )

    @Schema(description = "요리 주재료 수정 요청 DTO")
    data class UpdateMainIngredientDto(
        @field:Size(max = 50, message = "요리 주재료는 최대 50자까지 가능합니다.")
        @Schema(description = "요리 주재료 명칭", example = "소고기")
        val ingredient: String?
    )
}
