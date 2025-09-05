package zipbap.app.api.recipe.validator

import org.springframework.stereotype.Component
import zipbap.app.api.recipe.dto.RecipeRequestDto
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
import zipbap.app.global.exception.GeneralException
import zipbap.app.global.code.status.ErrorStatus

/**
 * 레시피 작성 시 필요한 카테고리들을 검증하는 Validator
 */
data class ValidatedCategories(
    val myCategory: MyCategory?,
    val cookingType: CookingType,
    val situation: Situation,
    val mainIngredient: MainIngredient,
    val method: Method,
    val headcount: Headcount,
    val cookingTime: CookingTime,
    val level: Level
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
    fun validateAll(dto: RecipeRequestDto.RegisterRecipeRequestDto): ValidatedCategories {
        val myCategory = dto.myCategoryId?.let {
            myCategoryRepository.findById(it)
                .orElseThrow { GeneralException(ErrorStatus.CATEGORY_NOT_FOUND) }
        }

        val cookingType = cookingTypeRepository.findById(dto.cookingTypeId)
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TYPE_NOT_FOUND) }
        val situation = situationRepository.findById(dto.situationId)
            .orElseThrow { GeneralException(ErrorStatus.SITUATION_NOT_FOUND) }
        val mainIngredient = mainIngredientRepository.findById(dto.mainIngredientId)
            .orElseThrow { GeneralException(ErrorStatus.MAIN_INGREDIENT_NOT_FOUND) }
        val method = methodRepository.findById(dto.methodId)
            .orElseThrow { GeneralException(ErrorStatus.METHOD_NOT_FOUND) }
        val headcount = headcountRepository.findById(dto.headcountId)
            .orElseThrow { GeneralException(ErrorStatus.HEADCOUNT_NOT_FOUND) }
        val cookingTime = cookingTimeRepository.findById(dto.cookingTimeId)
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TIME_NOT_FOUND) }
        val level = levelRepository.findById(dto.levelId)
            .orElseThrow { GeneralException(ErrorStatus.LEVEL_NOT_FOUND) }

        return ValidatedCategories(
            myCategory, cookingType, situation,
            mainIngredient, method, headcount,
            cookingTime, level
        )
    }
}
