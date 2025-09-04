package zipbap.app.admin.category.level.converter

import zipbap.app.domain.category.level.Level
import zipbap.app.admin.category.level.dto.LevelRequestDto
import zipbap.app.admin.category.level.dto.LevelResponseDto

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
