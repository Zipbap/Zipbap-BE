package zipbap.app.api.category.dto

import io.swagger.v3.oas.annotations.media.Schema

object CategoryResponseDto {

    data class CategoryListResponseDto(
        @Schema(description = "요리 시간 목록")
        val cookingTimes: List<String>,

        @Schema(description = "요리 종류 목록")
        val cookingTypes: List<String>,

        @Schema(description = "인원 수 목록")
        val headcounts: List<String>,

        @Schema(description = "난이도 목록")
        val levels: List<String>,

        @Schema(description = "주재료 목록")
        val mainIngredients: List<String>,

        @Schema(description = "조리 방법 목록")
        val methods: List<String>,

        @Schema(description = "상황별 요리 목록")
        val situations: List<String>,

        @Schema(description = "내 카테고리 목록")
        val myCategories: List<String>
    )
}
