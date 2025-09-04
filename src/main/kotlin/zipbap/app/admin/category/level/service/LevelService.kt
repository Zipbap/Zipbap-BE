package zipbap.app.admin.category.level.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.admin.category.level.converter.LevelConverter
import zipbap.app.admin.category.level.dto.LevelRequestDto
import zipbap.app.admin.category.level.dto.LevelResponseDto
import zipbap.app.domain.category.level.LevelRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional
class LevelService(
    private val levelRepository: LevelRepository
) {

    /**
     * 새로운 난이도를 등록합니다.
     *
     * @param dto 새로운 난이도 생성 요청 DTO
     * @return 생성된 난이도 응답 DTO
     */
    fun createLevel(dto: LevelRequestDto.CreateLevelDto): LevelResponseDto {
        // 이미 존재하는 난이도일 경우
        if (levelRepository.existsByLevel(dto.level)) {
            throw GeneralException(ErrorStatus.DUPLICATE_LEVEL)
        }
        val entity = LevelConverter.toEntity(dto)
        return LevelConverter.toDto(levelRepository.save(entity))
    }

    /**
     * 특정 ID의 난이도를 수정합니다.
     *
     * @param id 수정할 난이도 ID
     * @param dto 난이도 수정 요청 DTO
     * @return 수정된 난이도 응답 DTO
     */
    fun updateLevel(id: Long, dto: LevelRequestDto.UpdateLevelDto): LevelResponseDto {
        val entity = levelRepository.findById(id)
            // 해당 ID의 난이도가 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.LEVEL_NOT_FOUND) }

        val updated = LevelConverter.toEntity(entity.id!!, dto)
        return LevelConverter.toDto(levelRepository.save(updated))
    }

    /**
     * 특정 ID의 난이도를 조회합니다.
     *
     * @param id 조회할 난이도 ID
     * @return 조회된 난이도 응답 DTO
     */
    @Transactional(readOnly = true)
    fun getLevel(id: Long): LevelResponseDto =
        levelRepository.findById(id)
            .map(LevelConverter::toDto)
            // 해당 ID의 난이도가 존재하지 않을 경우
            .orElseThrow { GeneralException(ErrorStatus.LEVEL_NOT_FOUND) }

    /**
     * 모든 난이도를 조회합니다.
     *
     * @return 난이도 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getAllLevels(): List<LevelResponseDto> =
        levelRepository.findAll()
            .map(LevelConverter::toDto)

    /**
     * 특정 ID의 난이도를 삭제합니다.
     *
     * @param id 삭제할 난이도 ID
     */
    fun deleteLevel(id: Long) {
        // 해당 ID의 난이도가 존재하지 않을 경우
        if (!levelRepository.existsById(id)) {
            throw GeneralException(ErrorStatus.LEVEL_NOT_FOUND)
        }
        levelRepository.deleteById(id)
    }
}
