package zipbap.app.api.mycategory.converter

import zipbap.app.domain.category.mycategory.MyCategory
import zipbap.app.api.mycategory.dto.MyCategoryRequestDto
import zipbap.app.api.mycategory.dto.MyCategoryResponseDto
import zipbap.app.domain.user.User

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
