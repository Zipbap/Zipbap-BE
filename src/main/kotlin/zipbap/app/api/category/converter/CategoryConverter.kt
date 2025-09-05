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
            cookingTimes = cookingTimes.map { CategoryResponseDto.CategoryItemDto(it.id, it.cookingTime) },
            cookingTypes = cookingTypes.map { CategoryResponseDto.CategoryItemDto(it.id, it.type) },
            headcounts = headcounts.map { CategoryResponseDto.CategoryItemDto(it.id, it.headcount) },
            levels = levels.map { CategoryResponseDto.CategoryItemDto(it.id, it.level) },
            mainIngredients = mainIngredients.map { CategoryResponseDto.CategoryItemDto(it.id, it.ingredient) },
            methods = methods.map { CategoryResponseDto.CategoryItemDto(it.id, it.method) },
            situations = situations.map { CategoryResponseDto.CategoryItemDto(it.id, it.situation) },
            myCategories = myCategories.map { CategoryResponseDto.MyCategoryItemDto(it.id, it.name) }
        )
}
