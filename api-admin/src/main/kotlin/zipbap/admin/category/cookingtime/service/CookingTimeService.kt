// CookingTimeService.kt
package zipbap.admin.category.cookingtime.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.admin.category.cookingtime.converter.CookingTimeConverter
import zipbap.admin.category.cookingtime.dto.CookingTimeRequestDto
import zipbap.admin.category.cookingtime.dto.CookingTimeResponseDto
import zipbap.global.domain.category.cookingtime.CookingTimeRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional
class CookingTimeService(
    private val cookingTimeRepository: CookingTimeRepository
) {

    /**
     * 새로운 요리 시간을 등록합니다.
     *
     * @param dto 새로운 요리 시간 생성 요청 DTO
     * @return 생성된 요리 시간 응답 DTO
     */
    fun createCookingTime(dto: CookingTimeRequestDto.CreateCookingTimeDto): CookingTimeResponseDto {
        // 이미 존재하는 요리 시간일 경우
        if (cookingTimeRepository.existsByCookingTime(dto.cookingTime)) {
            throw GeneralException(ErrorStatus.DUPLICATE_COOKING_TIME)
        }
        val entity = CookingTimeConverter.toEntity(dto)
        return CookingTimeConverter.toDto(cookingTimeRepository.save(entity))
    }

    /**
     * 특정 ID의 요리 시간을 수정합니다.
     *
     * @param id 수정할 요리 시간 ID
     * @param dto 요리 시간 수정 요청 DTO
     * @return 수정된 요리 시간 응답 DTO
     */
    fun updateCookingTime(id: Long, dto: CookingTimeRequestDto.UpdateCookingTimeDto): CookingTimeResponseDto {
        val entity = cookingTimeRepository.findById(id)
            // 해당 ID의 요리 시간이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TIME_NOT_FOUND) }

        val updated = CookingTimeConverter.toEntity(entity.id!!, dto)
        return CookingTimeConverter.toDto(cookingTimeRepository.save(updated))
    }

    /**
     * 특정 ID의 요리 시간을 조회합니다.
     *
     * @param id 조회할 요리 시간 ID
     * @return 조회된 요리 시간 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getCookingTime(id: Long): CookingTimeResponseDto =
        cookingTimeRepository.findById(id)
            .map(CookingTimeConverter::toDto)
            // 해당 ID의 요리 시간이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TIME_NOT_FOUND) }

    /**
     * 모든 요리 시간을 조회합니다.
     *
     * @return 요리 시간 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllCookingTimes(): List<CookingTimeResponseDto> =
        cookingTimeRepository.findAll()
            .map(CookingTimeConverter::toDto)

    /**
     * 특정 ID의 요리 시간을 삭제합니다.
     *
     * @param id 삭제할 요리 시간 ID
     */
    fun deleteCookingTime(id: Long) {
        // 해당 ID의 요리 시간이 존재하지 않을 경우
        if (!cookingTimeRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.COOKING_TIME_NOT_FOUND)
        }
        cookingTimeRepository.deleteById(id)
    }
}
