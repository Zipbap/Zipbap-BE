package zipbap.app.admin.category.situation.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.admin.category.situation.converter.SituationConverter
import zipbap.app.admin.category.situation.dto.SituationRequestDto
import zipbap.app.admin.category.situation.dto.SituationResponseDto
import zipbap.app.domain.category.situation.SituationRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional
class SituationService(
    private val situationRepository: SituationRepository
) {

    /**
     * 새로운 요리 상황 카테고리를 등록합니다.
     *
     * @param dto 새로운 요리 상황 생성 요청 DTO
     * @return 생성된 요리 상황 응답 DTO
     */
    fun createSituation(dto: SituationRequestDto.CreateSituationDto): SituationResponseDto {
        // 이미 존재하는 요리 상황일 경우
        if (situationRepository.existsBySituation(dto.situation)) {
            throw GeneralException(ErrorStatus.DUPLICATE_SITUATION)
        }
        val entity = SituationConverter.toEntity(dto)
        return SituationConverter.toDto(situationRepository.save(entity))
    }

    /**
     * 특정 ID의 요리 상황 카테고리를 수정합니다.
     *
     * @param id 수정할 요리 상황 ID
     * @param dto 요리 상황 수정 요청 DTO
     * @return 수정된 요리 상황 응답 DTO
     */
    fun updateSituation(id: Long, dto: SituationRequestDto.UpdateSituationDto): SituationResponseDto {
        // 해당 ID의 요리 상황이 존재하지 않을 경우
        val entity = situationRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.SITUATION_NOT_FOUND) }

        val updated = SituationConverter.toEntity(entity.id!!, dto)
        return SituationConverter.toDto(situationRepository.save(updated))
    }

    /**
     * 특정 ID의 요리 상황 카테고리를 조회합니다.
     *
     * @param id 조회할 요리 상황 ID
     * @return 조회된 요리 상황 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getSituation(id: Long): SituationResponseDto =
        situationRepository.findById(id)
            .map(SituationConverter::toDto)
            // 해당 ID의 요리 상황이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.SITUATION_NOT_FOUND) }

    /**
     * 모든 요리 상황 카테고리를 조회합니다.
     *
     * @return 요리 상황 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllSituations(): List<SituationResponseDto> =
        situationRepository.findAll()
            .map(SituationConverter::toDto)

    /**
     * 특정 ID의 요리 상황 카테고리를 삭제합니다.
     *
     * @param id 삭제할 요리 상황 ID
     */
    fun deleteSituation(id: Long) {
        // 해당 ID의 요리 상황이 존재하지 않을 경우
        if (!situationRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.SITUATION_NOT_FOUND)
        }
        situationRepository.deleteById(id)
    }
}
