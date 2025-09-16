package zipbap.user.api.mycategory.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MyCategoryRequestDto {

    @Schema(description = "내 카테고리 생성 요청 DTO")
    data class CreateMyCategoryDto(
        @field:NotBlank(message = "내 카테고리 이름은 필수 값입니다.")
        @field:Size(max = 20, message = "내 카테고리 이름은 최대 20자까지 가능합니다.")
        @Schema(description = "내 카테고리 명칭", example = "다이어트 요리")
        val name: String,
    )

    @Schema(description = "내 카테고리 수정 요청 DTO")
    data class UpdateMyCategoryDto(
        @field:Size(max = 20, message = "내 카테고리 이름은 최대 20자까지 가능합니다.")
        @Schema(description = "내 카테고리 명칭", example = "간단한 요리")
        val name: String?,
    )
}
