package zipbap.app.api.category.converter

import zipbap.app.api.category.dto.CategoryResponseDto
import zipbap.app.domain.category.cookingtime.CookingTime
import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.domain.category.headcount.Headcount
import zipbap.app.domain.category.level.Level
import zipbap.app.domain.category.mainingredient.MainIngredient
import zipbap.app.domain.category.method.Method
import zipbap.app.domain.category.mycategory.MyCategory
import zipbap.app.domain.category.situation.Situation

object CategoryConverter {


    /**
     * 카테로별 Entity 리스트 -> CategoryListResponseDto
     */
    fun toCategoryListResponseDto(
        cookingTimes: List<CookingTime>,
        cookingTypes: List<CookingType>,
        headcounts: List<Headcount>,
        levels: List<Level>,
        mainIngredients: List<MainIngredient>,
        methods: List<Method>,
        situations: List<Situation>,
        myCategories: List<MyCategory>
    ): CategoryResponseDto.CategoryListResponseDto =
        CategoryResponseDto.CategoryListResponseDto(
            cookingTimes = cookingTimes.map { it.cookingTime },
            cookingTypes = cookingTypes.map { it.type },
            headcounts = headcounts.map { it.headcount },
            levels = levels.map { it.level },
            mainIngredients = mainIngredients.map { it.ingredient },
            methods = methods.map { it.method },
            situations = situations.map { it.situation },
            myCategories = myCategories.map { it.name }
        )
}
