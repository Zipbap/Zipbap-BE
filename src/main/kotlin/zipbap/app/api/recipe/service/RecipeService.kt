package zipbap.app.api.recipe.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.recipe.converter.RecipeConverter
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.domain.cookingorder.CookingOrderRepository
import zipbap.app.domain.recipe.RecipeRepository
import zipbap.app.domain.user.UserRepository
import zipbap.app.domain.category.cookingtime.CookingTimeRepository
import zipbap.app.domain.category.cookingtype.CookingTypeRepository
import zipbap.app.domain.category.headcount.HeadcountRepository
import zipbap.app.domain.category.level.LevelRepository
import zipbap.app.domain.category.mainingredient.MainIngredientRepository
import zipbap.app.domain.category.method.MethodRepository
import zipbap.app.domain.category.mycategory.MyCategoryRepository
import zipbap.app.domain.category.situation.SituationRepository
import zipbap.app.global.util.CustomIdGenerator
import zipbap.app.global.exception.GeneralException
import zipbap.app.global.code.status.ErrorStatus

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val cookingOrderRepository: CookingOrderRepository,
    private val userRepository: UserRepository,
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
     * 레시피 생성
     */
    @Transactional
    fun createRecipe(
        dto: RecipeRequestDto.CreateRecipeRequest,
        userId: Long
    ): RecipeResponseDto.CreateRecipeResponse {
        // 사용자 확인
        if (!userRepository.existsById(userId)) {
            throw GeneralException(ErrorStatus.BAD_REQUEST)
        }
        val sequence = recipeRepository.countByUserId(userId) + 1
        val generatedId = CustomIdGenerator.generate("RC", userId, sequence)
        val userRef = userRepository.getReferenceById(userId)

        // myCategory는 선택값
        val myCategory = dto.myCategoryId?.let { id ->
            myCategoryRepository.findById(id)
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

        val recipe = RecipeConverter.toEntity(
            id = generatedId,
            user = userRef,
            dto = dto,
            myCategory = myCategory,
            cookingType = cookingType,
            situation = situation,
            mainIngredient = mainIngredient,
            method = method,
            headcount = headcount,
            cookingTime = cookingTime,
            level = level
        )
        val savedRecipe = recipeRepository.save(recipe)

        val orderEntities = RecipeConverter.toCookingOrderEntities(savedRecipe, dto.cookingOrders)
        val savedOrders = cookingOrderRepository.saveAll(orderEntities)

        return RecipeConverter.toCreateResponse(savedRecipe, savedOrders)
    }
}
