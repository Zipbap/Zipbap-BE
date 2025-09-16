package zipbap.user.api.mycategory.converter

import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.user.api.mycategory.dto.MyCategoryRequestDto
import zipbap.user.api.mycategory.dto.MyCategoryResponseDto
import zipbap.global.domain.user.User

object MyCategoryConverter {

    /**
     * CreateMyCategoryDto -> MyCategoryEntity
     */
    fun toEntity(id: String, dto: MyCategoryRequestDto.CreateMyCategoryDto, user: User): MyCategory =
        MyCategory(id = id, user = user, name = dto.name)

    /**
     * UpdateMyCategoryDto -> MyCategoryEntity
     */
    fun toEntity(id: String, dto: MyCategoryRequestDto.UpdateMyCategoryDto, user: User): MyCategory =
        MyCategory(id = id, user = user, name = dto.name ?: "")

    /**
     * MyCategoryEntity -> MyCategoryResponseDto
     */
    fun toDto(entity: MyCategory): MyCategoryResponseDto =
        MyCategoryResponseDto(
            id = entity.id,
            name = entity.name,
        )
}
