package zipbap.admin.category.method.converter

import zipbap.admin.category.method.dto.MethodRequestDto
import zipbap.admin.category.method.dto.MethodResponseDto
import zipbap.global.domain.category.method.Method

object MethodConverter {

    /**
     * CreateMethodDto -> MethodEntity
     */
    fun toEntity(dto: MethodRequestDto.CreateMethodDto): Method =
        Method(method = dto.method)

    /**
     * UpdateMethodDto -> MethodEntity
     */
    fun toEntity(id: Long, dto: MethodRequestDto.UpdateMethodDto): Method =
        Method(id = id, method = dto.method ?: "")

    /**
     * MethodEntity -> MethodResponseDto
     */
    fun toDto(entity: Method): MethodResponseDto =
        MethodResponseDto(
            id = entity.id ?: 0L,
            method = entity.method
        )
}
