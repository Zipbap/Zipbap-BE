package zipbap.app.api.recipe.converter

import zipbap.app.api.recipe.dto.RecipeRequestDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.domain.cookingorder.CookingOrder
import zipbap.app.domain.recipe.Recipe
import zipbap.app.domain.recipe.RecipeStatus
import zipbap.app.domain.user.User
import zipbap.app.domain.category.cookingtime.CookingTime
import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.domain.category.headcount.Headcount
import zipbap.app.domain.category.level.Level
import zipbap.app.domain.category.mainingredient.MainIngredient
import zipbap.app.domain.category.method.Method
import zipbap.app.domain.category.mycategory.MyCategory
import zipbap.app.domain.category.situation.Situation

object RecipeConverter {

    /**
     * RegisterRecipeRequestDto -> Recipe Entity
     */
    fun toEntity(
        id: String,
        user: User,
        dto: RecipeRequestDto.RegisterRecipeRequestDto,
        myCategory: MyCategory?,
        cookingType: CookingType,
        situation: Situation,
        mainIngredient: MainIngredient,
        method: Method,
        headcount: Headcount,
        cookingTime: CookingTime,
        level: Level
    ): Recipe =
        Recipe(
            id = id,
            user = user,
            title = dto.title,
            subtitle = dto.subtitle,
            introduction = dto.introduction,
            myCategory = myCategory,
            cookingType = cookingType,
            situation = situation,
            mainIngredient = mainIngredient,
            method = method,
            headcount = headcount,
            cookingTime = cookingTime,
            level = level,
            ingredientInfo = dto.ingredientInfo,
            video = dto.video,
            kick = dto.kick,
            isPrivate = dto.isPrivate,
            recipeStatus = RecipeStatus.ACTIVE
        )

    /**
     * CookingOrderRequest List -> CookingOrder Entity List
     */
    fun toCookingOrderEntities(
        recipe: Recipe,
        orders: List<RecipeRequestDto.RegisterRecipeRequestDto.CookingOrderRequest>
    ): List<CookingOrder> =
        orders.sortedBy { it.turn }
            .map {
                CookingOrder(
                    recipe = recipe,
                    image = it.image,
                    description = it.description,
                    turn = it.turn
                )
            }

    /**
     * Recipe Entity + CookingOrder List -> RecipeDetailResponseDto (최종 등록 레시피)
     */
    fun toDetailResponse(
        recipe: Recipe,
        orders: List<CookingOrder>
    ): RecipeResponseDto.RecipeDetailResponseDto =
        RecipeResponseDto.RecipeDetailResponseDto(
            id = recipe.id,
            title = recipe.title ?: "",
            subtitle = recipe.subtitle ?: "",
            introduction = recipe.introduction ?: "",
            myCategoryId = recipe.myCategory?.id,
            cookingTypeId = recipe.cookingType?.id ?: 0L,
            situationId = recipe.situation?.id ?: 0L,
            mainIngredientId = recipe.mainIngredient?.id ?: 0L,
            methodId = recipe.method?.id ?: 0L,
            headcountId = recipe.headcount?.id ?: 0L,
            cookingTimeId = recipe.cookingTime?.id ?: 0L,
            levelId = recipe.level?.id ?: 0L,
            ingredientInfo = recipe.ingredientInfo ?: "",
            kick = recipe.kick,
            isPrivate = recipe.isPrivate,
            video = recipe.video,
            cookingOrders = orders.sortedBy { it.turn }.map {
                RecipeResponseDto.RecipeDetailResponseDto.CookingOrderResponse(
                    turn = it.turn,
                    image = it.image,
                    description = it.description
                )
            }
        )

    /**
     * Recipe Entity + CookingOrder List -> TempRecipeDetailResponseDto (임시 저장 레시피)
     */
    fun toTempDetailResponse(
        recipe: Recipe,
        orders: List<CookingOrder>
    ): RecipeResponseDto.TempRecipeDetailResponseDto =
        RecipeResponseDto.TempRecipeDetailResponseDto(
            id = recipe.id,
            title = recipe.title,
            subtitle = recipe.subtitle,
            introduction = recipe.introduction,
            myCategoryId = recipe.myCategory?.id,
            cookingTypeId = recipe.cookingType?.id,
            situationId = recipe.situation?.id,
            mainIngredientId = recipe.mainIngredient?.id,
            methodId = recipe.method?.id,
            headcountId = recipe.headcount?.id,
            cookingTimeId = recipe.cookingTime?.id,
            levelId = recipe.level?.id,
            ingredientInfo = recipe.ingredientInfo,
            kick = recipe.kick,
            isPrivate = recipe.isPrivate,
            video = recipe.video,
            cookingOrders = orders.sortedBy { it.turn }.map {
                RecipeResponseDto.TempRecipeDetailResponseDto.CookingOrderResponse(
                    turn = it.turn,
                    image = it.image,
                    description = it.description
                )
            }
        )
}
