package zipbap.admin.category.cookingtype.converter

import zipbap.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.admin.category.cookingtype.dto.CookingTypeResponseDto
import zipbap.global.domain.category.cookingtype.CookingType

object CookingTypeConverter {

    /**
     * CreateCookingTypeDto -> CookingTypeEntity
     */
    fun toEntity(dto: CookingTypeRequestDto.CreateCookingTypeDto): CookingType =
        CookingType(type = dto.type)

    /**
     * UpdateCookingTypeDto -> CookingTypeEntity
     */
    fun toEntity(id: Long, dto: CookingTypeRequestDto.UpdateCookingTypeDto): CookingType =
        CookingType(id = id, type = dto.type ?: "")

    /**
     * CookingTypeEntity-> CookingTypeResponseDto
     */
    fun toDto(entity: CookingType): CookingTypeResponseDto =
        CookingTypeResponseDto(
            id = entity.id ?: 0L,
            type = entity.type
        )
}
