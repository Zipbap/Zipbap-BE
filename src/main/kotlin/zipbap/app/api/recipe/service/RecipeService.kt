package zipbap.app.api.recipe.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.recipe.converter.RecipeConverter
import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.recipe.validator.CategoryValidator
import zipbap.app.domain.cookingorder.CookingOrderRepository
import zipbap.app.domain.file.FileEntity
import zipbap.app.domain.file.FileRepository
import zipbap.app.domain.file.FileStatus
import zipbap.app.domain.recipe.Recipe
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
    private val categoryValidator: CategoryValidator,
    private val fileRepository: FileRepository,
) {

    /**
     * 임시 레시피 생성
     */
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

        // 요청에서 사용된 파일 URL 수집
        val usedFileUrls = mutableSetOf<String>()
        dto.video?.let { usedFileUrls.add(it) }
        dto.cookingOrders?.forEach { order -> order.image?.let { usedFileUrls.add(it) } }

        // 파일 상태 업데이트 (임시 저장은 TEMPORARY_UPLOAD 유지)
        updateFileStatuses(recipeId, usedFileUrls, FileStatus.TEMPORARY_UPLOAD, recipe)

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
        updateFileStatuses(recipeId, usedFileUrls, FileStatus.FINALIZED, recipe)

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

    /**
     * 임시 저장 레시피 조회
     */
    @Transactional(readOnly = true)
    fun getMyTempRecipes(userId: Long): List<RecipeResponseDto.TempRecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.TEMPORARY)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.recipeId)
            RecipeConverter.toTempDto(recipe, orders)
        }
    }

    /**
     * 최종 저장 레시피 조회
     */
    @Transactional(readOnly = true)
    fun getMyRecipes(userId: Long): List<RecipeResponseDto.RecipeDetailResponseDto> {
        val recipes = recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.ACTIVE)
        return recipes.map { recipe ->
            val orders = cookingOrderRepository.findAllByRecipeId(recipe.recipeId)
            RecipeConverter.toDto(recipe, orders)
        }
    }

    /**
     * 공통 파일 상태 업데이트 유틸
     * - 사용되지 않는 파일 → UNTRACKED
     * - 사용된 파일 → 주어진 targetStatus 로 업데이트
     */
    private fun updateFileStatuses(
        recipeId: String,
        usedFileUrls: Set<String>,
        targetStatus: FileStatus,
        recipe: Recipe
    ) {
        // 기존에 연결된 파일들
        val attachedFiles = fileRepository.findAllByRecipeId(recipeId)

        // 사용 안 된 파일은 UNTRACKED 처리
        attachedFiles.filter { it.fileUrl !in usedFileUrls }.forEach { file: FileEntity ->
            file.status = FileStatus.UNTRACKED
            file.recipe = null
            fileRepository.save(file)
        }

        // 사용된 파일은 targetStatus 로 변경
        usedFileUrls.forEach { url ->
            fileRepository.findByFileUrl(url)?.apply {
                this.recipe = recipe
                this.status = targetStatus
                fileRepository.save(this)
            }
        }
    }

    /**
     * 레시피 단일 조회 (본인 소유 & ACTIVE만)
     */
    @Transactional
    fun getRecipeDetail(
        recipeId: String,
        userId: Long
    ): RecipeResponseDto.RecipeDetailResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.user.id != userId) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }
        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_NOT_FOUND)
        }

        // 조회수 증가
        recipe.viewCount += 1
        val savedRecipe = recipeRepository.save(recipe)

        val orders = cookingOrderRepository.findAllByRecipeId(recipe.recipeId)
        return RecipeConverter.toDto(savedRecipe, orders)
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
        val recipes = if (myCategoryIds.isNullOrEmpty()) {
            recipeRepository.findAllByUserIdAndRecipeStatus(userId, RecipeStatus.ACTIVE)
        } else {
            recipeRepository.findAllByUserIdAndRecipeStatusAndMyCategoryIdIn(
                userId,
                RecipeStatus.ACTIVE,
                myCategoryIds.toSet()
            )
        }

        return recipes.map { RecipeConverter.toListItemDto(it) }
    }

    @Transactional(readOnly = true)
    fun getFeedList(userId: Long): List<RecipeResponseDto.FeedResponseDto> {
        val feedList = recipeRepository.findAllFeed(userId, RecipeStatus.ACTIVE, false)

        return feedList.map {
            RecipeConverter.toFeedDto(it)
        }.toList()
    }
}