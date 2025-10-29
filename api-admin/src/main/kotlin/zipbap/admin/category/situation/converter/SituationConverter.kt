package zipbap.admin.category.situation.converter

import zipbap.admin.category.situation.dto.SituationRequestDto
import zipbap.admin.category.situation.dto.SituationResponseDto
import zipbap.global.domain.category.situation.Situation

object SituationConverter {

    /**
     * CreateSituationDto -> SituationEntity
     */
    fun toEntity(dto: SituationRequestDto.CreateSituationDto): Situation =
        Situation(situation = dto.situation)

    /**
     * UpdateSituationDto -> SituationEntity
     */
    fun toEntity(id: Long, dto: SituationRequestDto.UpdateSituationDto): Situation =
        Situation(id = id, situation = dto.situation ?: "")

    /**
     * SituationEntity -> SituationResponseDto
     */
    fun toDto(entity: Situation): SituationResponseDto =
        SituationResponseDto(
            id = entity.id ?: 0L,
            situation = entity.situation
        )
}
