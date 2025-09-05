package zipbap.app.api.recipe.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.recipe.converter.RecipeConverter
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.recipe.validator.CategoryValidator
import zipbap.app.domain.cookingorder.CookingOrderRepository
import zipbap.app.domain.recipe.RecipeRepository
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.util.CustomIdGenerator
import zipbap.app.global.exception.GeneralException
import zipbap.app.global.code.status.ErrorStatus

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val cookingOrderRepository: CookingOrderRepository,
    private val userRepository: UserRepository,
    private val categoryValidator: CategoryValidator
) {

    @Transactional
    fun registerRecipe(
        dto: RecipeRequestDto.RegisterRecipeRequestDto,
        userId: Long
    ): RecipeResponseDto.RecipeDetailResponseDto {
        if (!userRepository.existsById(userId)) {
            throw GeneralException(ErrorStatus.BAD_REQUEST)
        }

        val sequence = recipeRepository.countByUserId(userId) + 1
        val generatedId = CustomIdGenerator.generate("RC", userId, sequence)
        val userRef = userRepository.getReferenceById(userId)

        // 🔹 카테고리 검증
        val categories = categoryValidator.validateAll(dto)

        val recipe = RecipeConverter.toEntity(
            id = generatedId,
            user = userRef,
            dto = dto,
            myCategory = categories.myCategory,
            cookingType = categories.cookingType,
            situation = categories.situation,
            mainIngredient = categories.mainIngredient,
            method = categories.method,
            headcount = categories.headcount,
            cookingTime = categories.cookingTime,
            level = categories.level
        )

        val savedRecipe = recipeRepository.save(recipe)
        val orderEntities = RecipeConverter.toCookingOrderEntities(savedRecipe, dto.cookingOrders)
        val savedOrders = cookingOrderRepository.saveAll(orderEntities)

        return RecipeConverter.toDetailResponse(savedRecipe, savedOrders)
    }
}
