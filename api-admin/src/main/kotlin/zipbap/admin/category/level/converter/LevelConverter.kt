package zipbap.admin.category.level.converter

import zipbap.admin.category.level.dto.LevelRequestDto
import zipbap.admin.category.level.dto.LevelResponseDto
import zipbap.global.domain.category.level.Level

object LevelConverter {

    /**
     * CreateLevelDto -> LevelEntity
     */
    fun toEntity(dto: LevelRequestDto.CreateLevelDto): Level =
        Level(level = dto.level)

    /**
     * UpdateLevelDto -> LevelEntity
     */
    fun toEntity(id: Long, dto: LevelRequestDto.UpdateLevelDto): Level =
        Level(id = id, level = dto.level ?: "")

    /**
     * LevelEntity -> LevelResponseDto
     */
    fun toDto(entity: Level): LevelResponseDto =
        LevelResponseDto(
            id = entity.id ?: 0L,
            level = entity.level
        )
}
