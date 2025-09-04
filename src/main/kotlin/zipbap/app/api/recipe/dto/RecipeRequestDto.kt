package zipbap.app.api.recipe.dto

import io.swagger.v3.oas.annotations.media.Schema

object RecipeRequestDto {

    data class CreateRecipeRequest(

        @Schema(description = "레시피 제목", example = "소고기 미역국", required = true)
        val title: String,

        @Schema(description = "레시피 소제목", example = "할머니의 손맛", required = true)
        val subtitle: String,

        @Schema(description = "레시피 소개", example = "제 생일이면 늘 할머니가 끓여주신 추억의 레시피입니다.", required = true)
        val introduction: String,

        // 카테고리 필드들 (ID만 받음)
        @Schema(description = "내 레시피(해당 사용자 전용)", example = "MC-1-00001")
        val myCategoryId: String? = null,

        @Schema(description = "요리종류 ID", example = "1", required = true)
        val cookingTypeId: Long,

        @Schema(description = "상황 카테고리 ID", example = "1", required = true)
        val situationId: Long,

        @Schema(description = "주재료 카테고리 ID", example = "1", required = true)
        val mainIngredientId: Long,

        @Schema(description = "조리방법 카테고리 ID", example = "1", required = true)
        val methodId: Long,

        @Schema(description = "인분(인원수) 카테고리 ID", example = "1", required = true)
        val headcountId: Long,

        @Schema(description = "요리 시간 카테고리 ID", example = "1", required = true)
        val cookingTimeId: Long,

        @Schema(description = "난이도 카테고리 ID", example = "1", required = true)
        val levelId: Long,

        @Schema(description = "재료 정보", example = "소고기 200g, 미역 50g, 다진 마늘 1큰술, 국간장 2큰술, 소금 약간", required = true)
        val ingredientInfo: String,

        @Schema(description = "요리 킥", example = "소고기를 먼저 볶아주면 국물 맛이 깊어집니다.")
        val kick: String? = null,

        @Schema(description = "비공개 여부", example = "true", required = true)
        val isPrivate: Boolean,

        @Schema(description = "조리 순서 목록")
        val cookingOrders: List<CookingOrderRequest> = emptyList(),

        @Schema(description = "영상 URL", example = "https://cdn.zipbap.store/recipes/food.mp4")
        val video: String? = null
    ) {
        data class CookingOrderRequest(
            @Schema(description = "순서", example = "1")
            val turn: Int,

            @Schema(description = "이미지 URL", example = "https://cdn.zipbap.store/recipes/step1.jpg")
            val image: String? = null,

            @Schema(description = "설명", example = "불린 미역을 참기름에 살짝 볶아줍니다.")
            val description: String
        )
    }
}
