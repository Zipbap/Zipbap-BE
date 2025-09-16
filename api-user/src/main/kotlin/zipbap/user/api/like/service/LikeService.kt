package zipbap.user.api.like.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.like.dto.LikeResponseDto
import zipbap.global.domain.like.RecipeLike
import zipbap.global.domain.like.RecipeLikeRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.User
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.code.status.ErrorStatus

@Service
class LikeService(
        private val recipeLikeRepository: RecipeLikeRepository,
        private val recipeRepository: RecipeRepository
) {

    /**
     * 특정 레시피에 좋아요를 추가합니다.
     * 단, 레시피 상태가 ACTIVE 인 경우에만 가능하며 중복 좋아요는 허용되지 않습니다.
     *
     * @param user    좋아요를 누른 사용자 엔티티
     * @param recipeId 좋아요를 추가할 레시피 ID (예: "RC-1-00001")
     * @return 레시피 ID와 현재 좋아요 수
     */
    @Transactional
    fun likeRecipe(user: User, recipeId: String): LikeResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (recipeLikeRepository.existsByUserAndRecipe(user, recipe)) {
            throw GeneralException(ErrorStatus.ALREADY_LIKED_RECIPE)
        }

        recipeLikeRepository.save(RecipeLike(user, recipe))
        val count = recipeLikeRepository.countByRecipe(recipe)

        return LikeResponseDto(recipe.recipeId, count)
    }

    /**
     * 특정 레시피에 대한 좋아요를 취소합니다.
     *
     * @param user    좋아요를 취소하는 사용자 엔티티
     * @param recipeId 좋아요를 취소할 레시피 ID (예: "RC-1-00001")
     * @return 레시피 ID와 현재 좋아요 수
     */
    @Transactional
    fun unlikeRecipe(user: User, recipeId: String): LikeResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (!recipeLikeRepository.existsByUserAndRecipe(user, recipe)) {
            throw GeneralException(ErrorStatus.LIKE_NOT_FOUND)
        }

        recipeLikeRepository.deleteByUserAndRecipe(user, recipe)
        val count = recipeLikeRepository.countByRecipe(recipe)

        return LikeResponseDto(recipe.recipeId, count)
    }

    /**
     * 특정 레시피의 좋아요 개수를 조회합니다.
     *
     * @param recipeId 좋아요 개수를 조회할 레시피 ID (예: "RC-1-00001")
     * @return 레시피 ID와 현재 좋아요 수
     */
    @Transactional(readOnly = true)
    fun countLikes(recipeId: String): LikeResponseDto {
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        val count = recipeLikeRepository.countByRecipe(recipe)
        return LikeResponseDto(recipe.recipeId, count)
    }
}
