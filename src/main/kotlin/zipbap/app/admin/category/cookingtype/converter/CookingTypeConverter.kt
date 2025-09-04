package zipbap.app.admin.category.cookingtype.converter

import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.app.admin.category.cookingtype.dto.CookingTypeResponseDto

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
