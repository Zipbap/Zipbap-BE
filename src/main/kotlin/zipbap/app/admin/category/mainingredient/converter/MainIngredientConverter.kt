package zipbap.app.admin.category.mainingredient.converter

import zipbap.app.domain.category.mainingredient.MainIngredient
import zipbap.app.admin.category.mainingredient.dto.MainIngredientRequestDto
import zipbap.app.admin.category.mainingredient.dto.MainIngredientResponseDto

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
