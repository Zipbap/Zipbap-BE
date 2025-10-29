package zipbap.admin.category.mainingredient.converter

import zipbap.admin.category.mainingredient.dto.MainIngredientRequestDto
import zipbap.admin.category.mainingredient.dto.MainIngredientResponseDto
import zipbap.global.domain.category.mainingredient.MainIngredient

object MainIngredientConverter {

    /**
     * CreateMainIngredientDto -> MainIngredientEntity
     */
    fun toEntity(dto: MainIngredientRequestDto.CreateMainIngredientDto): MainIngredient =
        MainIngredient(ingredient = dto.ingredient)

    /**
     * UpdateMainIngredientDto -> MainIngredientEntity
     */
    fun toEntity(id: Long, dto: MainIngredientRequestDto.UpdateMainIngredientDto): MainIngredient =
        MainIngredient(id = id, ingredient = dto.ingredient ?: "")

    /**
     * MainIngredientEntity -> MainIngredientResponseDto
     */
    fun toDto(entity: MainIngredient): MainIngredientResponseDto =
        MainIngredientResponseDto(
            id = entity.id ?: 0L,
            ingredient = entity.ingredient
        )
}
