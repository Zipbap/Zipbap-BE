package zipbap.admin.category.mainingredient.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.admin.category.mainingredient.converter.MainIngredientConverter
import zipbap.admin.category.mainingredient.dto.MainIngredientRequestDto
import zipbap.admin.category.mainingredient.dto.MainIngredientResponseDto
import zipbap.global.domain.category.mainingredient.MainIngredientRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional
class MainIngredientService(
    private val mainIngredientRepository: MainIngredientRepository
) {

    /**
     * 새로운 요리 주재료를 등록합니다.
     *
     * @param dto 새로운 요리 주재료 생성 요청 DTO
     * @return 생성된 요리 주재료 응답 DTO
     */
    fun createMainIngredient(dto: MainIngredientRequestDto.CreateMainIngredientDto): MainIngredientResponseDto {
        // 이미 존재하는 요리 주재료일 경우
        if (mainIngredientRepository.existsByIngredient(dto.ingredient)) {
            throw GeneralException(ErrorStatus.DUPLICATE_MAIN_INGREDIENT)
        }
        val entity = MainIngredientConverter.toEntity(dto)
        return MainIngredientConverter.toDto(mainIngredientRepository.save(entity))
    }

    /**
     * 특정 ID의 요리 주재료를 수정합니다.
     *
     * @param id 수정할 요리 주재료 ID
     * @param dto 요리 주재료 수정 요청 DTO
     * @return 수정된 요리 주재료 응답 DTO
     */
    fun updateMainIngredient(id: Long, dto: MainIngredientRequestDto.UpdateMainIngredientDto): MainIngredientResponseDto {
        // 해당 ID의 요리 주재료가 존재하지 않을 경우
        val entity = mainIngredientRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.MAIN_INGREDIENT_NOT_FOUND) }

        val updated = MainIngredientConverter.toEntity(entity.id!!, dto)
        return MainIngredientConverter.toDto(mainIngredientRepository.save(updated))
    }

    /**
     * 특정 ID의 요리 주재료를 조회합니다.
     *
     * @param id 조회할 요리 주재료 ID
     * @return 조회된 요리 주재료 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getMainIngredient(id: Long): MainIngredientResponseDto =
        mainIngredientRepository.findById(id)
            .map(MainIngredientConverter::toDto)
            // 해당 ID의 요리 주재료가 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.MAIN_INGREDIENT_NOT_FOUND) }

    /**
     * 모든 요리 주재료를 조회합니다.
     *
     * @return 요리 주재료 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllMainIngredients(): List<MainIngredientResponseDto> =
        mainIngredientRepository.findAll()
            .map(MainIngredientConverter::toDto)

    /**
     * 특정 ID의 요리 주재료를 삭제합니다.
     *
     * @param id 삭제할 요리 주재료 ID
     */
    fun deleteMainIngredient(id: Long) {
        // 해당 ID의 요리 주재료가 존재하지 않을 경우
        if (!mainIngredientRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.MAIN_INGREDIENT_NOT_FOUND)
        }
        mainIngredientRepository.deleteById(id)
    }
}
