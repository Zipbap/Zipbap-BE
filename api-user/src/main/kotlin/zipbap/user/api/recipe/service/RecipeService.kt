package zipbap.user.api.recipe.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.recipe.converter.RecipeConverter
import zipbap.user.api.recipe.dto.RecipeRequestDto
import zipbap.user.api.recipe.dto.RecipeResponseDto
import zipbap.user.api.recipe.validator.CategoryValidator
import zipbap.global.domain.cookingorder.CookingOrderRepository
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import zipbap.global.domain.recipe.*
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.util.CustomIdGenerator
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.code.status.ErrorStatus
import zipbap.user.api.recipe.event.RecipeViewedEvent

@Service
class RecipeService(
        private val recipeRepository: RecipeRepository,
        private val recipeQueryRepository: RecipeQueryRepository,
        private val cookingOrderRepository: CookingOrderRepository,
        private val userRepository: UserRepository,
        private val categoryValidator: CategoryValidator,
        private val recipeFileService: RecipeFileService,
        private val publisher: ApplicationEventPublisher
) {

    /**
     * 임시 레시피 생성
     */
    @Transactional
    fun createTempRecipe(userId: Long): RecipeResponseDto.TempRecipeDetailResponseDto {
        if (!userRepository.existsById(userId)) {
            throw GeneralException(ErrorStatus.RECIPE_BAD_REQUEST)
        }

        val tmp = recipeRepository.findTopByUser_IdOrderByIdDesc(userId)
        val sequence = (tmp?.id?.split("-")?.lastOrNull()?.toLongOrNull() ?: 0L) + 1

        val generatedId = CustomIdGenerator.generate("RC", userId, sequence)
        val userRef = userRepository.getReferenceById(userId)

        val recipe = RecipeConverter.toEntity(generatedId, userRef)
        val savedRecipe = recipeRepository.save(recipe)

        return RecipeConverter.toTempDto(savedRecipe, emptyList())
    }

    /**
     * 임시 레시피 수정
     */
    @Transactional
    fun updateTempRecipe(
            dto: RecipeRequestDto.UpdateTempRecipeRequestDto,
            userId: Long,
            recipeId: String
    ): RecipeResponseDto.TempRecipeDetailResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.user.id != userId) throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)

        val categories = categoryValidator.validateOptional(dto)

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()
        dto.video?.let { usedFileUrls.add(it) }
        dto.cookingOrders?.forEach { order -> order.image?.let { usedFileUrls.add(it) } }

        // 파일 상태 업데이트 (임시 저장은 TEMPORARY_UPLOAD 유지)
        recipeFileService.updateFileStatuses(recipeId, usedFileUrls, FileStatus.TEMPORARY_UPLOAD, recipe)

        // 레시피 기본 필드 업데이트
        recipe.apply {
            thumbnail = dto.thumbnail ?: thumbnail
            title = dto.title ?: title
            subtitle = dto.subtitle ?: subtitle
            introduction = dto.introduction ?: introduction
            ingredientInfo = dto.ingredientInfo ?: ingredientInfo
            video = dto.video ?: video
            kick = dto.kick ?: kick
            isPrivate = dto.isPrivate ?: isPrivate

            categories.myCategory?.let { myCategory = it }
            categories.cookingType?.let { cookingType = it }
            categories.situation?.let { situation = it }
            categories.mainIngredient?.let { mainIngredient = it }
            categories.method?.let { method = it }
            categories.headcount?.let { headcount = it }
            categories.cookingTime?.let { cookingTime = it }
            categories.level?.let { level = it }

        }

        val savedRecipe = recipeRepository.save(recipe)

        // cookingOrders 재저장
        cookingOrderRepository.deleteAllByRecipeId(recipeId)
        cookingOrderRepository.flush()
        val orderEntities = dto.cookingOrders?.let {
            RecipeConverter.toEntityFromUpdate(savedRecipe, it)
        } ?: emptyList()
        val savedOrders = cookingOrderRepository.saveAll(orderEntities)

        return RecipeConverter.toTempDto(savedRecipe, savedOrders)
    }

    /**
     * 최종 레시피 저장
     */
    @Transactional
    fun finalizeRecipe(
        recipeId: String,
        userId: Long,
        dto: RecipeRequestDto.FinalizeRecipeRequestDto
    ): RecipeResponseDto.RecipeDetailResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.user.id != userId) throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)

        val categories = categoryValidator.validateAll(dto, strict = true)

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()
        dto.video?.let { usedFileUrls.add(it) }
        dto.cookingOrders.forEach { order -> order.image?.let { usedFileUrls.add(it) } }

        // 파일 상태 업데이트 (최종 저장은 FINALIZED 처리)
        recipeFileService.updateFileStatuses(recipeId, usedFileUrls, FileStatus.FINALIZED, recipe)

        // 레시피 엔티티 업데이트
        recipe.apply {
            thumbnail = dto.thumbnail
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

        // cookingOrders 재저장
        cookingOrderRepository.deleteAllByRecipeId(recipeId)
        cookingOrderRepository.flush()
        val orderEntities = RecipeConverter.toEntityFromRegister(savedRecipe, dto.cookingOrders)
        val savedOrders = cookingOrderRepository.saveAll(orderEntities)

        return RecipeConverter.toDto(savedRecipe, savedOrders)
    }

    @Transactional
    fun deleteRecipe(recipeId: String,
                     userId: Long) {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }
        if (recipe.user.id != userId) throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        recipeFileService.deleteFileStatuses(recipeId, recipe)
        recipeRepository.delete(recipe)
    }

    /**
     * 임시 저장 레시피 조회
     */
    @Transactional(readOnly = true)
    fun getMyTempRecipes(userId: Long): List<RecipeResponseDto.TempRecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.TEMPORARY)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.id)
            RecipeConverter.toTempDto(recipe, orders)
        }
    }

    /**
     * MyTempRecipes 조회의 경량버전
     * 화면을 참고해서 화면에 필요한 최소한의 데이터만 리턴하는 버전입니다.
     * 기존 모두 조회는 임시레시피가 아무것도 작성되지 않았다면 괜찮지만, 여러 카테고리 등 연관관계를 갖고 있는 경우
     * 각 임시 레시피별로 cookingOrder 조회 + 카테고리 조회로 인해 조회쿼리가 폭발하므로 이를 개선하기 위한 버전입니다.
     */
    @Transactional(readOnly = true)
    fun getMyTempRecipesV2(userId: Long): List<RecipeResponseDto.TempRecipeSummaryResponseDto> {
        val searchCondition = RecipeSearchCondition(userId = userId, status = RecipeStatus.TEMPORARY)
        val recipes = recipeQueryRepository.findRecipes(searchCondition)

        return recipes.map { recipe ->
            RecipeConverter.toTempSummaryDto(recipe)
        }
    }

    /**
     * 최종 저장 레시피 조회
     */
    @Transactional(readOnly = true)
    fun getMyRecipes(userId: Long): List<RecipeResponseDto.RecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.ACTIVE)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.id)
            RecipeConverter.toDto(recipe, orders)
        }
    }

    /**
     * 최정 저장 레시피 조회의 경량버전입니다.
     */
    @Transactional(readOnly = true)
    fun getMyRecipesV2(userId: Long): List<RecipeResponseDto.RecipeSummaryResponseDto> {
        val searchCondition = RecipeSearchCondition(userId = userId, status = RecipeStatus.ACTIVE)
        val recipes = recipeQueryRepository.findRecipes(searchCondition)
        return recipes.map { recipe ->
            RecipeConverter.toSummaryDto(recipe)
        }
    }

    /**
     * 레시피 단일 조회 (본인 소유 & ACTIVE만)
     */
    @Transactional(readOnly = true)
    fun getRecipeDetail(
        recipeId: String,
        userId: Long
    ): RecipeResponseDto.RecipeDetailResponseDto {
        val recipe = recipeQueryRepository.findRecipeDetail(recipeId)
                ?: throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)

        if (recipe.user.id != userId) { // 검증 로직이 꼭 필요할까?
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }
        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)
        }

        val orders = cookingOrderRepository.findAllByRecipeId(recipe.id)

        publisher.publishEvent(RecipeViewedEvent(recipeId, userId))
        return RecipeConverter.toDto(recipe, orders)
    }

    /**
     * 내 ACTIVE 레시피 목록 조회 (다중 myCategory 필터)
     * - myCategoryIds가 null/빈 리스트면 전체 ACTIVE 반환
     * - 값이 있으면 myCategory.id IN (...) 필터
     */
    @Transactional(readOnly = true)
    fun getMyActiveRecipesFiltered(
        userId: Long,
        myCategoryIds: List<String>?
    ): List<RecipeResponseDto.MyRecipeListItemResponseDto> {
//        val recipes = if (myCategoryIds.isNullOrEmpty()) {
//            recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.ACTIVE)
//        } else {
//            recipeRepository.findAllByUserIdAndRecipeStatusAndMyCategoryIdIn(
//                userId,
//                RecipeStatus.ACTIVE,
//                myCategoryIds.toSet()
//            )
//        }
        val searchCondition = RecipeSearchCondition(
                userId = userId,
                status = RecipeStatus.ACTIVE,
                myCategoryIds = myCategoryIds)

        val recipes = recipeQueryRepository.findRecipes(searchCondition)

        return recipes.map { RecipeConverter.toListItemDto(it) }
    }



}