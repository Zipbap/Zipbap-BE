package zipbap.app.admin.category.headcount.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.admin.category.headcount.converter.HeadcountConverter
import zipbap.app.admin.category.headcount.dto.HeadcountRequestDto
import zipbap.app.admin.category.headcount.dto.HeadcountResponseDto
import zipbap.app.domain.category.headcount.HeadcountRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional
class HeadcountService(
    private val headcountRepository: HeadcountRepository
) {

    /**
     * 새로운 인분을 등록합니다.
     *
     * @param dto 새로운 인분 생성 요청 DTO
     * @return 생성된 인분 응답 DTO
     */
    fun createHeadcount(dto: HeadcountRequestDto.CreateHeadcountDto): HeadcountResponseDto {
        // 이미 존재하는 인분일 경우
        if (headcountRepository.existsByHeadcount(dto.headcount)) {
            throw GeneralException(ErrorStatus.DUPLICATE_HEADCOUNT)
        }
        val entity = HeadcountConverter.toEntity(dto)
        return HeadcountConverter.toDto(headcountRepository.save(entity))
    }

    /**
     * 특정 ID의 인분을 수정합니다.
     *
     * @param id 수정할 인분 ID
     * @param dto 인분 수정 요청 DTO
     * @return 수정된 인분 응답 DTO
     */
    fun updateHeadcount(id: Long, dto: HeadcountRequestDto.UpdateHeadcountDto): HeadcountResponseDto {
        val entity = headcountRepository.findById(id)
            // 해당 ID의 요리 인분이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.HEADCOUNT_NOT_FOUND) }

        val updated = HeadcountConverter.toEntity(entity.id!!, dto)
        return HeadcountConverter.toDto(headcountRepository.save(updated))
    }

    /**
     * 특정 ID의 인분을 조회합니다.
     *
     * @param id 조회할 인분 ID
     * @return 조회된 인분 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getHeadcount(id: Long): HeadcountResponseDto =
        headcountRepository.findById(id)
            .map(HeadcountConverter::toDto)
            // 해당 ID의 요리 인분이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.HEADCOUNT_NOT_FOUND) }

    /**
     * 모든 인분을 조회합니다.
     *
     * @return 인분 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllHeadcounts(): List<HeadcountResponseDto> =
        headcountRepository.findAll()
            .map(HeadcountConverter::toDto)

    /**
     * 특정 ID의 인분을 삭제합니다.
     *
     * @param id 삭제할 인분 ID
     */
    fun deleteHeadcount(id: Long) {
        // 해당 ID의 요리 인분이 존재하지 않을 경우
        if (!headcountRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.HEADCOUNT_NOT_FOUND)
        }
        headcountRepository.deleteById(id)
    }
}
