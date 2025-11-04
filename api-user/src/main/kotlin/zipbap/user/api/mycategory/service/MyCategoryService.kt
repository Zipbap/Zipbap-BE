package zipbap.user.api.mycategory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.mycategory.converter.MyCategoryConverter
import zipbap.user.api.mycategory.dto.MyCategoryRequestDto
import zipbap.user.api.mycategory.dto.MyCategoryResponseDto
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.category.mycategory.MyCategoryRepository
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.util.CustomIdGenerator

@Service
@Transactional
class MyCategoryService(
        private val myCategoryRepository: MyCategoryRepository,
        private val userRepository: UserRepository
) {

    /**
     * 새로운 내 카테고리를 등록합니다.
     *
     * @param dto 새로운 내 카테고리 생성 요청 DTO
     * @param userId 요청한 사용자 ID (추후 JWT 인증 사용자 기반으로 변경 예정)
     * @return 생성된 내 카테고리 응답 DTO
     */
    fun createMyCategory(dto: MyCategoryRequestDto.CreateMyCategoryDto, userId: Long): MyCategoryResponseDto {
        // 사용자 미인증 시
        val user: User = userRepository.findById(userId)
            .orElseThrow { GeneralException(ErrorStatus.UNAUTHORIZED) }

        // 이미 존재하는 '내 카테고리'일 경우
        if (myCategoryRepository.existsByUserIdAndName(userId, dto.name)) {
            throw GeneralException(ErrorStatus.DUPLICATE_CATEGORY)
        }

        // 사용자별 카테고리 수 → 다음 시퀀스
        val seq = myCategoryRepository.findMaxSequenceByUserId(userId)
        val id = CustomIdGenerator.generate("MC", userId, seq)

        val entity = MyCategory(id = id, user = user, name = dto.name)
        return MyCategoryConverter.toDto(myCategoryRepository.save(entity))
    }

    /**
     * 특정 ID의 내 카테고리를 수정합니다.
     *
     * @param id 수정할 내 카테고리 ID
     * @param dto 내 카테고리 수정 요청 DTO
     * @param userId 요청한 사용자 ID (추후 JWT 인증 사용자 기반으로 변경 예정)
     * @return 수정된 내 카테고리 응답 DTO
     */
    fun updateMyCategory(id: String, dto: MyCategoryRequestDto.UpdateMyCategoryDto, userId: Long): MyCategoryResponseDto {
        // 해당 ID의 '내 카테고리'가 존재하지 않을 경우
        val entity = myCategoryRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.CATEGORY_NOT_FOUND) }

        // 다른 사용자가 소유한 카테고리 수정 시도
        if (entity.user.id != userId) {
            throw GeneralException(ErrorStatus.FORBIDDEN)
        }

        // 사용자 미인증 시
        val user: User = userRepository.findById(userId)
            .orElseThrow { GeneralException(ErrorStatus.UNAUTHORIZED) }

        val updated = MyCategory(id = entity.id, user = user, name = dto.name ?: "")
        return MyCategoryConverter.toDto(myCategoryRepository.save(updated))
    }

    /**
     * 특정 사용자의 모든 내 카테고리를 조회합니다.
     *
     * @param userId 요청한 사용자 ID (추후 JWT 인증 사용자 기반으로 변경 예정)
     * @return 해당 사용자의 내 카테고리 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getMyCategories(userId: Long): List<MyCategoryResponseDto> {
        // 사용자 미인증 시
        val user: User = userRepository.findById(userId)
            .orElseThrow { GeneralException(ErrorStatus.UNAUTHORIZED) }

        return myCategoryRepository.findAll()
            .filter { it.user.id == user.id }
            .map(MyCategoryConverter::toDto)
    }

    /**
     * 특정 ID의 내 카테고리를 삭제합니다.
     *
     * @param id 삭제할 내 카테고리 ID
     * @param userId 요청한 사용자 ID (추후 JWT 인증 사용자 기반으로 변경 예정)
     */
    fun deleteMyCategory(id: String, userId: Long) {
        // 해당 ID의 '내 카테고리'가 존재하지 않을 경우
        val entity = myCategoryRepository.findById(id)
            .orElseThrow { GeneralException(ErrorStatus.CATEGORY_NOT_FOUND) }

        // 다른 사용자가 소유한 카테고리 삭제 시도
        if (entity.user.id != userId) {
            throw GeneralException(ErrorStatus.FORBIDDEN)
        }

        myCategoryRepository.deleteById(id)
    }
}
