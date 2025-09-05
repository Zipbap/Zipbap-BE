package zipbap.app.api.recipe.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 레시피 요청 DTO 모음
 */
object RecipeRequestDto {

    /**
     * 레시피 최종 등록 요청 DTO
     * 모든 필드 필수
     */
    data class FinalizeRecipeRequestDto(

        @Schema(description = "레시피 제목", example = "소고기 미역국", required = true)
        val title: String,

        @Schema(description = "레시피 소제목", example = "할머니의 손맛", required = true)
        val subtitle: String,

        @Schema(description = "레시피 소개", example = "제 생일이면 늘 할머니가 끓여주신 추억의 레시피입니다.", required = true)
        val introduction: String,

        @Schema(description = "내 레시피(해당 사용자 전용)", example = "MC-1-00001")
        override val myCategoryId: String? = null,

        @Schema(description = "요리종류 ID", example = "1", required = true)
        override val cookingTypeId: Long,

        @Schema(description = "상황 카테고리 ID", example = "1", required = true)
        override val situationId: Long,

        @Schema(description = "주재료 카테고리 ID", example = "1", required = true)
        override val mainIngredientId: Long,

        @Schema(description = "조리방법 카테고리 ID", example = "1", required = true)
        override val methodId: Long,

        @Schema(description = "인분(인원수) 카테고리 ID", example = "1", required = true)
        override val headcountId: Long,

        @Schema(description = "요리 시간 카테고리 ID", example = "1", required = true)
        override val cookingTimeId: Long,

        @Schema(description = "난이도 카테고리 ID", example = "1", required = true)
        override val levelId: Long,

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
    ) : CategoryValidatable {

        data class CookingOrderRequest(
            @Schema(description = "순서", example = "1")
            val turn: Int,

            @Schema(description = "이미지 URL", example = "https://cdn.zipbap.store/recipes/step1.jpg")
            val image: String? = null,

            @Schema(description = "설명", example = "불린 미역을 참기름에 살짝 볶아줍니다.")
            val description: String
        )
    }

    /**
     * 임시 저장된 레시피 수정 요청 DTO
     * 모든 필드 선택값(null 허용)
     */
    data class UpdateTempRecipeRequestDto(

        @Schema(description = "레시피 제목", example = "소고기 미역국", required = false)
        val title: String? = null,

        @Schema(description = "레시피 소제목", example = "할머니의 손맛", required = false)
        val subtitle: String? = null,

        @Schema(description = "레시피 소개", example = "제 생일이면 늘 할머니가 끓여주신 추억의 레시피입니다.", required = false)
        val introduction: String? = null,

        @Schema(description = "내 레시피(해당 사용자 전용)", example = "MC-1-00001", required = false)
        override val myCategoryId: String? = null,

        @Schema(description = "요리종류 ID", example = "1", required = false)
        override val cookingTypeId: Long? = null,

        @Schema(description = "상황 카테고리 ID", example = "1", required = false)
        override val situationId: Long? = null,

        @Schema(description = "주재료 카테고리 ID", example = "1", required = false)
        override val mainIngredientId: Long? = null,

        @Schema(description = "조리방법 카테고리 ID", example = "1", required = false)
        override val methodId: Long? = null,

        @Schema(description = "인분(인원수) 카테고리 ID", example = "1", required = false)
        override val headcountId: Long? = null,

        @Schema(description = "요리 시간 카테고리 ID", example = "1", required = false)
        override val cookingTimeId: Long? = null,

        @Schema(description = "난이도 카테고리 ID", example = "1", required = false)
        override val levelId: Long? = null,

        @Schema(description = "재료 정보", example = "소고기 200g, 미역 50g, 다진 마늘 1큰술", required = false)
        val ingredientInfo: String? = null,

        @Schema(description = "요리 킥", example = "소고기를 먼저 볶아주면 국물 맛이 깊어집니다.", required = false)
        val kick: String? = null,

        @Schema(description = "비공개 여부", example = "true", required = false)
        val isPrivate: Boolean? = null,

        @Schema(description = "조리 순서 목록", required = false)
        val cookingOrders: List<CookingOrderRequest>? = null,

        @Schema(description = "영상 URL", example = "https://cdn.zipbap.store/recipes/food.mp4", required = false)
        val video: String? = null
    ) : CategoryValidatable {

        data class CookingOrderRequest(
            @Schema(description = "순서", example = "1", required = false)
            val turn: Int? = null,

            @Schema(description = "이미지 URL", example = "https://cdn.zipbap.store/recipes/step1.jpg", required = false)
            val image: String? = null,

            @Schema(description = "설명", example = "불린 미역을 참기름에 살짝 볶아줍니다.", required = false)
            val description: String? = null
        )
    }

}
