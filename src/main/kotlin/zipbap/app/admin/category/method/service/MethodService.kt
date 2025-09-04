package zipbap.app.admin.category.method.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.admin.category.method.converter.MethodConverter
import zipbap.app.admin.category.method.dto.MethodRequestDto
import zipbap.app.admin.category.method.dto.MethodResponseDto
import zipbap.app.domain.category.method.MethodRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional
class MethodService(
    private val methodRepository: MethodRepository
) {

    /**
     * 새로운 요리 방법을 등록합니다.
     *
     * @param dto 새로운 요리 방법 생성 요청 DTO
     * @return 생성된 요리 방법 응답 DTO
     */
    fun createMethod(dto: MethodRequestDto.CreateMethodDto): MethodResponseDto {
        // 이미 존재하는 요리 방법일 경우
        if (methodRepository.existsByMethod(dto.method)) {
            throw GeneralException(ErrorStatus.DUPLICATE_METHOD)
        }
        val entity = MethodConverter.toEntity(dto)
        return MethodConverter.toDto(methodRepository.save(entity))
    }

    /**
     * 특정 ID의 요리 방법을 수정합니다.
     *
     * @param id 수정할 요리 방법 ID
     * @param dto 요리 방법 수정 요청 DTO
     * @return 수정된 요리 방법 응답 DTO
     */
    fun updateMethod(id: Long, dto: MethodRequestDto.UpdateMethodDto): MethodResponseDto {
        // 해당 ID의 요리 방법이 존재하지 않을 경우
        val entity = methodRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.METHOD_NOT_FOUND) }

        val updated = MethodConverter.toEntity(entity.id!!, dto)
        return MethodConverter.toDto(methodRepository.save(updated))
    }

    /**
     * 특정 ID의 요리 방법을 조회합니다.
     *
     * @param id 조회할 요리 방법 ID
     * @return 조회된 요리 방법 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getMethod(id: Long): MethodResponseDto =
        methodRepository.findById(id)
            .map(MethodConverter::toDto)
            // 해당 ID의 요리 방법이 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.METHOD_NOT_FOUND) }

    /**
     * 모든 요리 방법을 조회합니다.
     *
     * @return 요리 방법 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllMethods(): List<MethodResponseDto> =
        methodRepository.findAll()
            .map(MethodConverter::toDto)

    /**
     * 특정 ID의 요리 방법을 삭제합니다.
     *
     * @param id 삭제할 요리 방법 ID
     */
    fun deleteMethod(id: Long) {
        // 해당 ID의 요리 방법이 존재하지 않을 경우
        if (!methodRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.METHOD_NOT_FOUND)
        }
        methodRepository.deleteById(id)
    }
}
