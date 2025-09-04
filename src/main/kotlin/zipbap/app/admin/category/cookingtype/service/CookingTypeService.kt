package zipbap.app.admin.category.cookingtype.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.admin.category.cookingtype.converter.CookingTypeConverter
import zipbap.app.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.app.admin.category.cookingtype.dto.CookingTypeResponseDto
import zipbap.app.domain.category.cookingtype.CookingTypeRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional
class CookingTypeService(
    private val cookingTypeRepository: CookingTypeRepository
) {

    /**
     * 새로운 요리 종류를 등록합니다.
     *
     * @param dto 새로운 요리 종류 생성 요청 DTO
     * @return 생성된 요리 종류 응답 DTO
     */
    fun createCookingType(dto: CookingTypeRequestDto.CreateCookingTypeDto): CookingTypeResponseDto {
        // 이미 존재하는 요리 종류일 경우
        if (cookingTypeRepository.existsByType(dto.type)) {
            throw GeneralException(ErrorStatus.DUPLICATE_COOKING_TYPE)
        }
        val entity = CookingTypeConverter.toEntity(dto)
        return CookingTypeConverter.toDto(cookingTypeRepository.save(entity))
    }

    /**
     * 특정 ID의 요리 종류를 수정합니다.
     *
     * @param id 수정할 요리 종류 ID
     * @param dto 요리 종류 수정 요청 DTO
     * @return 수정된 요리 종류 응답 DTO
     */
    fun updateCookingType(id: Long, dto: CookingTypeRequestDto.UpdateCookingTypeDto): CookingTypeResponseDto {
        // 해당 ID의 요리 종류가 존재하지 않을 경우
        val entity = cookingTypeRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TYPE_NOT_FOUND) }

        val updated = CookingTypeConverter.toEntity(entity.id!!, dto)
        return CookingTypeConverter.toDto(cookingTypeRepository.save(updated))
    }

    /**
     * 특정 ID의 요리 종류를 조회합니다.
     *
     * @param id 조회할 요리 종류 ID
     * @return 조회된 요리 종류 DTO
     */
    @Transactional(readOnly = true)
    fun getCookingType(id: Long): CookingTypeResponseDto =
        cookingTypeRepository.findById(id)
            .map(CookingTypeConverter::toDto)
            // 해당 ID의 요리 종류가 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.COOKING_TYPE_NOT_FOUND) }

    /**
     * 모든 요리 종류를 조회합니다.
     *
     * @return 모든 요리 종류 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllCookingTypes(): List<CookingTypeResponseDto> =
        cookingTypeRepository.findAll()
            .map(CookingTypeConverter::toDto)

    /**
     * 특정 ID의 요리 종류를 삭제합니다.
     *
     * @param id 삭제할 요리 종류 ID
     * @return 없음
     */
    fun deleteCookingType(id: Long) {
        // 해당 ID의 요리 유형이 존재하지 않을 경우
        if (!cookingTypeRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.COOKING_TYPE_NOT_FOUND)
        }
        cookingTypeRepository.deleteById(id)
    }
}
