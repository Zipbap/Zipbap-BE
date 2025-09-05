package zipbap.app.api.recipe.dto

/**
 * 카테고리 검증에 필요한 최소 필드만 제공하는 인터페이스
 */
interface CategoryValidatable {
    val myCategoryId: String?
    val cookingTypeId: Long?
    val situationId: Long?
    val mainIngredientId: Long?
    val methodId: Long?
    val headcountId: Long?
    val cookingTimeId: Long?
    val levelId: Long?
}
