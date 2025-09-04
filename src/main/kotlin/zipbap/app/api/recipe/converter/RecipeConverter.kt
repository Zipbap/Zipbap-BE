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

    fun toEntity(
        id: String,
        user: User,
        dto: RecipeRequestDto.CreateRecipeRequest,
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
            kick = dto.kick.orEmpty(),
            isPrivate = dto.isPrivate,
            recipeStatus = RecipeStatus.ACTIVE
        )

    fun toCookingOrderEntities(
        recipe: Recipe,
        orders: List<RecipeRequestDto.CreateRecipeRequest.CookingOrderRequest>
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

    fun toCreateResponse(
        recipe: Recipe,
        orders: List<CookingOrder>
    ): RecipeResponseDto.CreateRecipeResponse =
        RecipeResponseDto.CreateRecipeResponse(
            id = recipe.id,
            title = recipe.title,
            subtitle = recipe.subtitle,
            introduction = recipe.introduction,
            myCategoryId = recipe.myCategory?.id,
            cookingTypeId = recipe.cookingType.id!!,
            situationId = recipe.situation.id!!,
            mainIngredientId = recipe.mainIngredient.id!!,
            methodId = recipe.method.id!!,
            headcountId = recipe.headcount.id!!,
            cookingTimeId = recipe.cookingTime.id!!,
            levelId = recipe.level.id!!,
            ingredientInfo = recipe.ingredientInfo,
            kick = recipe.kick,
            isPrivate = recipe.isPrivate,
            video = recipe.video,
            cookingOrders = orders.sortedBy { it.turn }.map {
                RecipeResponseDto.CreateRecipeResponse.CookingOrderResponse(
                    turn = it.turn,
                    image = it.image,
                    description = it.description
                )
            }
        )
}
