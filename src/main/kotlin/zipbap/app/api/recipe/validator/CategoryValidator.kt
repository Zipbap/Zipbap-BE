package zipbap.app.api.recipe.validator

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import zipbap.app.api.recipe.dto.CategoryValidatable
import zipbap.app.domain.category.cookingtime.CookingTime
import zipbap.app.domain.category.cookingtime.CookingTimeRepository
import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.domain.category.cookingtype.CookingTypeRepository
import zipbap.app.domain.category.headcount.Headcount
import zipbap.app.domain.category.headcount.HeadcountRepository
import zipbap.app.domain.category.level.Level
import zipbap.app.domain.category.level.LevelRepository
import zipbap.app.domain.category.mainingredient.MainIngredient
import zipbap.app.domain.category.mainingredient.MainIngredientRepository
import zipbap.app.domain.category.method.Method
import zipbap.app.domain.category.method.MethodRepository
import zipbap.app.domain.category.mycategory.MyCategory
import zipbap.app.domain.category.mycategory.MyCategoryRepository
import zipbap.app.domain.category.situation.Situation
import zipbap.app.domain.category.situation.SituationRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

/**
 * 레시피 작성/수정 시 필요한 카테고리들을 검증하는 Validator
 */
data class ValidatedCategories(
    val myCategory: MyCategory?,
    val cookingType: CookingType?,
    val situation: Situation?,
    val mainIngredient: MainIngredient?,
    val method: Method?,
    val headcount: Headcount?,
    val cookingTime: CookingTime?,
    val level: Level?
)

@Component
class CategoryValidator(
    private val myCategoryRepository: MyCategoryRepository,
    private val cookingTypeRepository: CookingTypeRepository,
    private val situationRepository: SituationRepository,
    private val mainIngredientRepository: MainIngredientRepository,
    private val methodRepository: MethodRepository,
    private val headcountRepository: HeadcountRepository,
    private val cookingTimeRepository: CookingTimeRepository,
    private val levelRepository: LevelRepository
) {
    /**
     * strict = true → Finalize 요청 (모든 값 필수)
     * strict = false → Update(임시) 요청 (값이 들어온 경우만 검증)
     */
    fun validateAll(dto: CategoryValidatable, strict: Boolean): ValidatedCategories {
        val myCategory = validateOptional(dto.myCategoryId, myCategoryRepository, ErrorStatus.CATEGORY_NOT_FOUND)
        val cookingType = validateOptional(dto.cookingTypeId, cookingTypeRepository, ErrorStatus.COOKING_TYPE_NOT_FOUND)
        val situation = validateOptional(dto.situationId, situationRepository, ErrorStatus.SITUATION_NOT_FOUND)
        val mainIngredient = validateOptional(dto.mainIngredientId, mainIngredientRepository, ErrorStatus.MAIN_INGREDIENT_NOT_FOUND)
        val method = validateOptional(dto.methodId, methodRepository, ErrorStatus.METHOD_NOT_FOUND)
        val headcount = validateOptional(dto.headcountId, headcountRepository, ErrorStatus.HEADCOUNT_NOT_FOUND)
        val cookingTime = validateOptional(dto.cookingTimeId, cookingTimeRepository, ErrorStatus.COOKING_TIME_NOT_FOUND)
        val level = validateOptional(dto.levelId, levelRepository, ErrorStatus.LEVEL_NOT_FOUND)

        if (strict) {
            if (cookingType == null) throw GeneralException(ErrorStatus.COOKING_TYPE_NOT_FOUND)
            if (situation == null) throw GeneralException(ErrorStatus.SITUATION_NOT_FOUND)
            if (mainIngredient == null) throw GeneralException(ErrorStatus.MAIN_INGREDIENT_NOT_FOUND)
            if (method == null) throw GeneralException(ErrorStatus.METHOD_NOT_FOUND)
            if (headcount == null) throw GeneralException(ErrorStatus.HEADCOUNT_NOT_FOUND)
            if (cookingTime == null) throw GeneralException(ErrorStatus.COOKING_TIME_NOT_FOUND)
            if (level == null) throw GeneralException(ErrorStatus.LEVEL_NOT_FOUND)
        }

        return ValidatedCategories(
            myCategory, cookingType, situation,
            mainIngredient, method, headcount,
            cookingTime, level
        )
    }

    private fun <T, ID> validateOptional(
        id: ID?,
        repo: JpaRepository<T, ID>,
        error: ErrorStatus
    ): T? = id?.let {
        repo.findById(it).orElseThrow { GeneralException(error) }
    }
}
