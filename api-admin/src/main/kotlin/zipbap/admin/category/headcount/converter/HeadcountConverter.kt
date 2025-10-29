package zipbap.admin.category.headcount.converter

import zipbap.admin.category.headcount.dto.HeadcountRequestDto
import zipbap.admin.category.headcount.dto.HeadcountResponseDto
import zipbap.global.domain.category.headcount.Headcount

object HeadcountConverter {

    /**
     * CreateHeadcountDto -> HeadcountEntity
     */
    fun toEntity(dto: HeadcountRequestDto.CreateHeadcountDto): Headcount =
        Headcount(headcount = dto.headcount)

    /**
     * UpdateHeadcountDto -> HeadcountEntity
     */
    fun toEntity(id: Long, dto: HeadcountRequestDto.UpdateHeadcountDto): Headcount =
        Headcount(id = id, headcount = dto.headcount ?: "")

    /**
     * HeadcountEntity -> HeadcountResponseDto
     */
    fun toDto(entity: Headcount): HeadcountResponseDto =
        HeadcountResponseDto(
            id = entity.id ?: 0L,
            headcount = entity.headcount
        )
}
