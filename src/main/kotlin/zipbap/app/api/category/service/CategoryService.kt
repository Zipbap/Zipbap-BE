package zipbap.app.api.category.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.category.converter.CategoryConverter
import zipbap.app.api.category.dto.CategoryResponseDto
import zipbap.app.domain.category.cookingtime.CookingTimeRepository
import zipbap.app.domain.category.cookingtype.CookingTypeRepository
import zipbap.app.domain.category.headcount.HeadcountRepository
import zipbap.app.domain.category.level.LevelRepository
import zipbap.app.domain.category.mainingredient.MainIngredientRepository
import zipbap.app.domain.category.method.MethodRepository
import zipbap.app.domain.category.mycategory.MyCategoryRepository
import zipbap.app.domain.category.situation.SituationRepository
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
class CategoryService(
    private val cookingTimeRepository: CookingTimeRepository,
    private val cookingTypeRepository: CookingTypeRepository,
    private val headcountRepository: HeadcountRepository,
    private val levelRepository: LevelRepository,
    private val mainIngredientRepository: MainIngredientRepository,
    private val methodRepository: MethodRepository,
    private val situationRepository: SituationRepository,
    private val myCategoryRepository: MyCategoryRepository,
    private val userRepository: UserRepository
) {

    /**
     * 모든 카테고리 정보를 조회합니다.
     *
     * @param userId 카테고리를 조회할 사용자 ID
     * @return - CookingTime, CookingType, Headcount, Level, MainIngredient, Method, Situation 전체 목록
     *         - userId 에 해당하는 MyCategory 목록
     */
    @Transactional(readOnly = true)
    fun getAllCategories(userId: Long): CategoryResponseDto.CategoryListResponseDto {
        // 해당 하는 ID 사용자가 존재 하지 않을 때
        if (!userRepository.existsById(userId)) {
            throw GeneralException(ErrorStatus.BAD_REQUEST)
        }

        val cookingTimes = cookingTimeRepository.findAll()
        val cookingTypes = cookingTypeRepository.findAll()
        val headcounts = headcountRepository.findAll()
        val levels = levelRepository.findAll()
        val mainIngredients = mainIngredientRepository.findAll()
        val methods = methodRepository.findAll()
        val situations = situationRepository.findAll()
        val myCategories = myCategoryRepository.findAll()
            .filter { it.user.id == userId }

        return CategoryConverter.toCategoryListResponseDto(
            cookingTimes,
            cookingTypes,
            headcounts,
            levels,
            mainIngredients,
            methods,
            situations,
            myCategories
        )
    }
}
