package zipbap.app.api.recipe.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.recipe.converter.RecipeConverter
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.recipe.validator.CategoryValidator
import zipbap.app.domain.cookingorder.CookingOrderRepository
import zipbap.app.domain.recipe.RecipeRepository
import zipbap.app.domain.recipe.RecipeStatus
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
    fun createTempRecipe(userId: Long): RecipeResponseDto.TempRecipeDetailResponseDto {
        if (!userRepository.existsById(userId)) {
            throw GeneralException(ErrorStatus.RECIPE_BAD_REQUEST)
        }

        val sequence = recipeRepository.countByUserId(userId) + 1
        val generatedId = CustomIdGenerator.generate("RC", userId, sequence)
        val userRef = userRepository.getReferenceById(userId)

        val recipe = RecipeConverter.toEntity(generatedId, userRef)
        val savedRecipe = recipeRepository.save(recipe)

        return RecipeConverter.toTempDto(savedRecipe, emptyList())
    }

    @Transactional
    fun updateTempRecipe(
        dto: RecipeRequestDto.UpdateTempRecipeRequestDto,
        userId: Long,
        recipeId: String
    ): RecipeResponseDto.TempRecipeDetailResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.user.id != userId) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        // 1) turn 중복 방지(요청 내부, null 제외)
        val turns = dto.cookingOrders?.mapNotNull { it.turn } ?: emptyList()
        if (turns.size != turns.distinct().size) {
            throw GeneralException(ErrorStatus.DUPLICATE_COOKING_ORDER_TURN)
        }

        // 2) 카테고리 검증 (Update는 strict=false)
        val categories = categoryValidator.validateAll(dto, strict = false)

        // 3) 레시피 필드 업데이트 (TEMPORARY 유지)
        recipe.apply {
            title = dto.title ?: title
            subtitle = dto.subtitle ?: subtitle
            introduction = dto.introduction ?: introduction
            myCategory = categories.myCategory ?: myCategory
            cookingType = categories.cookingType ?: cookingType
            situation = categories.situation ?: situation
            mainIngredient = categories.mainIngredient ?: mainIngredient
            method = categories.method ?: method
            headcount = categories.headcount ?: headcount
            cookingTime = categories.cookingTime ?: cookingTime
            level = categories.level ?: level
            ingredientInfo = dto.ingredientInfo ?: ingredientInfo
            video = dto.video ?: video
            kick = dto.kick ?: kick
            isPrivate = dto.isPrivate ?: isPrivate
        }

        val savedRecipe = recipeRepository.save(recipe)

        // 4) cookingOrders가 null이면 기존 순서 유지, [] 또는 리스트면 전체 교체
        val savedOrders = if (dto.cookingOrders != null) {
            cookingOrderRepository.deleteAllByRecipeId(recipeId)
            cookingOrderRepository.flush()
            val orderEntities = RecipeConverter.toEntityFromUpdate(savedRecipe, dto.cookingOrders)
            cookingOrderRepository.saveAll(orderEntities)
        } else {
            // 기존 조리 순서 유지
            cookingOrderRepository.findAllByRecipeId(recipeId)
        }

        return RecipeConverter.toTempDto(savedRecipe, savedOrders)
    }

    @Transactional
    fun finalizeRecipe(
        recipeId: String,
        userId: Long,
        dto: RecipeRequestDto.finalizeRecipeRequestDto
    ): RecipeResponseDto.RecipeDetailResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.user.id != userId) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        // 이미 활성화된 레시피 재파이널 방지
        if (recipe.recipeStatus == RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_ALREADY_FINALIZED)
        }

        // 요청 내 turn 중복 방지
        val turns = dto.cookingOrders.map { it.turn }
        if (turns.size != turns.distinct().size) {
            throw GeneralException(ErrorStatus.DUPLICATE_COOKING_ORDER_TURN)
        }

        // 필수 카테고리/값 검증 (Finalize → strict = true)
        val categories = categoryValidator.validateAll(dto, strict = true)

        // 레시피 필드 채우고 ACTIVE로 전환
        recipe.apply {
            title = dto.title
            subtitle = dto.subtitle
            introduction = dto.introduction
            myCategory = categories.myCategory
            cookingType = categories.cookingType!!
            situation = categories.situation!!
            mainIngredient = categories.mainIngredient!!
            method = categories.method!!
            headcount = categories.headcount!!
            cookingTime = categories.cookingTime!!
            level = categories.level!!
            ingredientInfo = dto.ingredientInfo
            video = dto.video
            kick = dto.kick
            isPrivate = dto.isPrivate
            recipeStatus = RecipeStatus.ACTIVE
        }

        val savedRecipe = recipeRepository.save(recipe)

        // 기존 조리 순서 삭제 → flush → 새로 삽입
        cookingOrderRepository.deleteAllByRecipeId(recipeId)
        cookingOrderRepository.flush()

        val orderEntities = RecipeConverter.toEntityFromRegister(savedRecipe, dto.cookingOrders)
        val savedOrders = cookingOrderRepository.saveAll(orderEntities)

        return RecipeConverter.toDto(savedRecipe, savedOrders)
    }


    @Transactional(readOnly = true)
    fun getMyTempRecipes(userId: Long): List<RecipeResponseDto.TempRecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.TEMPORARY)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.id)
            RecipeConverter.toTempDto(recipe, orders)
        }
    }

    @Transactional(readOnly = true)
    fun getMyRecipes(userId: Long): List<RecipeResponseDto.RecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.ACTIVE)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.id)
            RecipeConverter.toDto(recipe, orders)
        }
    }

}
