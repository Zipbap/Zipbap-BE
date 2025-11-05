package zipbap.user.api.recipe.validator

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import zipbap.user.api.recipe.dto.CategoryValidatable
import zipbap.global.domain.category.cookingtime.CookingTime
import zipbap.global.domain.category.cookingtime.CookingTimeRepository
import zipbap.global.domain.category.cookingtype.CookingType
import zipbap.global.domain.category.cookingtype.CookingTypeRepository
import zipbap.global.domain.category.headcount.Headcount
import zipbap.global.domain.category.headcount.HeadcountRepository
import zipbap.global.domain.category.level.Level
import zipbap.global.domain.category.level.LevelRepository
import zipbap.global.domain.category.mainingredient.MainIngredient
import zipbap.global.domain.category.mainingredient.MainIngredientRepository
import zipbap.global.domain.category.method.Method
import zipbap.global.domain.category.method.MethodRepository
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.category.mycategory.MyCategoryRepository
import zipbap.global.domain.category.situation.Situation
import zipbap.global.domain.category.situation.SituationRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

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

    fun validateOptional(dto: CategoryValidatable): ValidatedCategories {
        val myCategory = validateOptional(dto.myCategoryId, myCategoryRepository, ErrorStatus.CATEGORY_NOT_FOUND)
        val cookingType = validateOptional(dto.cookingTypeId, cookingTypeRepository, ErrorStatus.COOKING_TYPE_NOT_FOUND)
        val situation = validateOptional(dto.situationId, situationRepository, ErrorStatus.SITUATION_NOT_FOUND)
        val mainIngredient = validateOptional(dto.mainIngredientId, mainIngredientRepository, ErrorStatus.MAIN_INGREDIENT_NOT_FOUND)
        val method = validateOptional(dto.methodId, methodRepository, ErrorStatus.METHOD_NOT_FOUND)
        val headcount = validateOptional(dto.headcountId, headcountRepository, ErrorStatus.HEADCOUNT_NOT_FOUND)
        val cookingTime = validateOptional(dto.cookingTimeId, cookingTimeRepository, ErrorStatus.COOKING_TIME_NOT_FOUND)
        val level = validateOptional(dto.levelId, levelRepository, ErrorStatus.LEVEL_NOT_FOUND)

        return ValidatedCategories(
            myCategory = myCategory,
            cookingType = cookingType,
            situation = situation,
            mainIngredient = mainIngredient,
            method = method,
            headcount = headcount,
            cookingTime = cookingTime,
            level = level
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
