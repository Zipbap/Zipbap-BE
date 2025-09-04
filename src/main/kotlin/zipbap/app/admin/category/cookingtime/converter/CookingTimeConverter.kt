package zipbap.app.admin.category.cookingtime.converter

import zipbap.app.domain.category.cookingtime.CookingTime
import zipbap.app.admin.category.cookingtime.dto.CookingTimeRequestDto
import zipbap.app.admin.category.cookingtime.dto.CookingTimeResponseDto

object CookingTimeConverter {

    /**
     * CreateCookingTimeDto -> CookingTimeEntity
     */
    fun toEntity(dto: CookingTimeRequestDto.CreateCookingTimeDto): CookingTime =
        CookingTime(cookingTime = dto.cookingTime)

    /**
     * UpdateCookingTimeDto -> CookingTimeEntity
     */
    fun toEntity(id: Long, dto: CookingTimeRequestDto.UpdateCookingTimeDto): CookingTime =
        CookingTime(id = id, cookingTime = dto.cookingTime ?: "")

    /**
     * CookingTimeEntity -> CookingTimeResponseDto
     */
    fun toDto(entity: CookingTime): CookingTimeResponseDto =
        CookingTimeResponseDto(
            id = entity.id ?: 0L,
            cookingTime = entity.cookingTime
        )
}
