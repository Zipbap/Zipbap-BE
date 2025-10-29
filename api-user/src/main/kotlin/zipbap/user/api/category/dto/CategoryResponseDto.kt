package zipbap.user.api.category.dto

import io.swagger.v3.oas.annotations.media.Schema

object CategoryResponseDto {

    @Schema(description = "카테고리 항목 DTO")
    data class CategoryItemDto(
        @Schema(description = "카테고리 ID", example = "1")
        val id: Long?,

        @Schema(description = "카테고리 이름/값", example = "간단 요리")
        val name: String
    )

    @Schema(description = "내 카테고리 항목 DTO")
    data class MyCategoryItemDto(
        @Schema(description = "내 카테고리 ID", example = "MC-1-00001")
        val id: String,

        @Schema(description = "내 카테고리 이름", example = "우리 가족 전용 레시피")
        val name: String
    )

    data class CategoryListResponseDto(
            @Schema(description = "요리 시간 목록")
        val cookingTimes: List<CategoryItemDto>,

            @Schema(description = "요리 종류 목록")
        val cookingTypes: List<CategoryItemDto>,

            @Schema(description = "인원 수 목록")
        val headcounts: List<CategoryItemDto>,

            @Schema(description = "난이도 목록")
        val levels: List<CategoryItemDto>,

            @Schema(description = "주재료 목록")
        val mainIngredients: List<CategoryItemDto>,

            @Schema(description = "조리 방법 목록")
        val methods: List<CategoryItemDto>,

            @Schema(description = "상황별 요리 목록")
        val situations: List<CategoryItemDto>,

            @Schema(description = "내 카테고리 목록")
        val myCategories: List<MyCategoryItemDto>
    )
}
