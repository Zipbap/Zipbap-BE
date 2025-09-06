package zipbap.app.api.recipe.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

object RecipeResponseDto {

    /**
     *  최종 등록된 레시피 상세 응답 DTO
     *  (단일 조회 시에도 null-safe 하도록 필드 nullable 처리)
     */
    data class RecipeDetailResponseDto(

        @Schema(description = "레시피 ID", example = "RC-1-00001")
        val id: String,

        @Schema(description = "레시피 제목", example = "소고기 미역국")
        val title: String? = null,

        @Schema(description = "대표 썸네일 URL", example = "https://cdn.zipbap.store/recipes/thumbnail.jpg")
        val thumbnail: String? = null,

        @Schema(description = "레시피 소제목", example = "할머니의 손맛")
        val subtitle: String? = null,

        @Schema(description = "레시피 소개", example = "제 생일이면 늘 할머니가 끓여주신 추억의 레시피입니다.")
        val introduction: String? = null,

        @Schema(description = "내 레시피(해당 사용자 전용)", example = "MC-1-00001")
        val myCategoryId: String? = null,

        @Schema(description = "요리종류 ID", example = "1")
        val cookingTypeId: Long? = null,

        @Schema(description = "상황 카테고리 ID", example = "1")
        val situationId: Long? = null,

        @Schema(description = "주재료 카테고리 ID", example = "2")
        val mainIngredientId: Long? = null,

        @Schema(description = "조리방법 카테고리 ID", example = "3")
        val methodId: Long? = null,

        @Schema(description = "인분(인원수) 카테고리 ID", example = "1")
        val headcountId: Long? = null,

        @Schema(description = "요리 시간 카테고리 ID", example = "2")
        val cookingTimeId: Long? = null,

        @Schema(description = "난이도 카테고리 ID", example = "1")
        val levelId: Long? = null,

        @Schema(description = "재료 정보", example = "소고기 200g, 미역 50g, 다진 마늘 1큰술, 국간장 2큰술, 소금 약간")
        val ingredientInfo: String? = null,

        @Schema(description = "요리 킥", example = "소고기를 먼저 볶아주면 국물 맛이 깊어집니다.")
        val kick: String? = null,

        @Schema(description = "비공개 여부", example = "true")
        val isPrivate: Boolean = true,

        @Schema(description = "생성 시각", example = "2025-09-01T12:34:56")
        val createdAt: LocalDateTime? = null,

        @Schema(description = "수정 시각", example = "2025-09-05T15:20:00")
        val updatedAt: LocalDateTime? = null,


        @Schema(description = "조리 순서 목록")
        val cookingOrders: List<CookingOrderResponse> = emptyList(),

        @Schema(description = "영상 URL", example = "https://cdn.zipbap.store/recipes/food.mp4")
        val video: String? = null
    ) {
        data class CookingOrderResponse(
            @Schema(description = "순서", example = "1")
            val turn: Int,

            @Schema(description = "이미지 URL", example = "https://cdn.zipbap.store/recipes/step1.jpg")
            val image: String? = null,

            @Schema(description = "설명", example = "불린 미역을 참기름에 살짝 볶아줍니다.")
            val description: String? = null
        )
    }

    /**
     *  임시 저장된 레시피 상세 응답 DTO
     */
    data class TempRecipeDetailResponseDto(

        @Schema(description = "레시피 ID", example = "RC-1-00002")
        val id: String,

        @Schema(description = "대표 썸네일 URL", example = "https://cdn.zipbap.store/recipes/temp_thumbnail.jpg")
        val thumbnail: String? = null,

        @Schema(description = "레시피 제목", example = "임시 소고기 미역국")
        val title: String? = null,

        @Schema(description = "레시피 소제목", example = "임시 저장본")
        val subtitle: String? = null,

        @Schema(description = "레시피 소개", example = "아직 작성 중인 레시피입니다.")
        val introduction: String? = null,

        @Schema(description = "내 레시피(해당 사용자 전용)", example = "MC-1-00001")
        val myCategoryId: String? = null,

        @Schema(description = "요리종류 ID", example = "1")
        val cookingTypeId: Long? = null,

        @Schema(description = "상황 카테고리 ID", example = "1")
        val situationId: Long? = null,

        @Schema(description = "주재료 카테고리 ID", example = "2")
        val mainIngredientId: Long? = null,

        @Schema(description = "조리방법 카테고리 ID", example = "3")
        val methodId: Long? = null,

        @Schema(description = "인분(인원수) 카테고리 ID", example = "1")
        val headcountId: Long? = null,

        @Schema(description = "요리 시간 카테고리 ID", example = "2")
        val cookingTimeId: Long? = null,

        @Schema(description = "난이도 카테고리 ID", example = "1")
        val levelId: Long? = null,

        @Schema(description = "재료 정보", example = "소고기 200g, 미역 50g")
        val ingredientInfo: String? = null,

        @Schema(description = "요리 킥", example = "아직 미정")
        val kick: String? = null,

        @Schema(description = "비공개 여부", example = "true")
        val isPrivate: Boolean? = null,

        @Schema(description = "생성 시각", example = "2025-09-01T12:34:56")
        val createdAt: LocalDateTime? = null,

        @Schema(description = "수정 시각", example = "2025-09-05T15:20:00")
        val updatedAt: LocalDateTime? = null,

        @Schema(description = "조리 순서 목록")
        val cookingOrders: List<CookingOrderResponse> = emptyList(),

        @Schema(description = "영상 URL", example = "https://cdn.zipbap.store/recipes/temp_food.mp4")
        val video: String? = null
    ) {
        data class CookingOrderResponse(
            @Schema(description = "순서", example = "1")
            val turn: Int,

            @Schema(description = "이미지 URL", example = "https://cdn.zipbap.store/recipes/step1.jpg")
            val image: String? = null,

            @Schema(description = "설명", example = "임시 설명입니다.")
            val description: String? = null
        )
    }

    /**
     * 목록/카드 뷰용 경량 DTO
     */
    data class MyRecipeListItemResponseDto(
        @Schema(description = "레시피 ID", example = "RC-1-00001")
        val id: String,

        @Schema(description = "대표 썸네일 URL", example = "https://cdn.zipbap.store/recipes/thumbnail.jpg")
        val thumbnail: String? = null,

        @Schema(description = "레시피 제목", example = "소고기 미역국")
        val title: String? = null,

        @Schema(description = "레시피 소제목", example = "할머니의 손맛")
        val subtitle: String? = null,

        @Schema(description = "레시피 소개", example = "제 생일이면 늘 할머니가 끓여주신 추억의 레시피입니다.")
        val introduction: String? = null,

        @Schema(description = "내 레시피 카테고리 ID", example = "MC-1-00001")
        val myCategoryId: String? = null,

        @Schema(description = "생성 시각", example = "2025-09-01T12:34:56")
        val createdAt: LocalDateTime? = null,

        @Schema(description = "수정 시각", example = "2025-09-05T15:20:00")
        val updatedAt: LocalDateTime? = null

    )
}
