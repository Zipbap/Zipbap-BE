package zipbap.app.api.boomark.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.api.like.dto.LikeResponseDto
import zipbap.app.domain.bookmark.Bookmark
import zipbap.app.domain.bookmark.BookmarkRepository
import zipbap.app.domain.like.RecipeLike
import zipbap.app.domain.recipe.RecipeRepository
import zipbap.app.domain.recipe.RecipeStatus
import zipbap.app.domain.user.User
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException
import zipbap.app.global.util.CustomIdGenerator

@Service
@Transactional(readOnly = true)
class BookmarkService(
        private val bookmarkRepository: BookmarkRepository,
        private val recipeRepository: RecipeRepository
) {

    /**
     * 특정 레시피에 북마크를 추가한다.
     * Recipe가 ACTIVE 상태여야 하고, 이전에 북마크한 기록이 있으면 안된다.
     */
    @Transactional
    fun markRecipe(user: User, recipeId: String): BookmarkResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (bookmarkRepository.existsByUserAndRecipe(user, recipe)) {
            throw GeneralException(ErrorStatus.ALREADY_LIKED_RECIPE)
        }

        val sequence = bookmarkRepository.countByUserId(user.id!!) + 1
        val generatedId = CustomIdGenerator.generate("BM", user.id!!, sequence)


        bookmarkRepository.save(Bookmark(user, recipe, generatedId))
        val count = bookmarkRepository.countByRecipe(recipe)

        return BookmarkResponseDto(recipe.id, count)
    }

    /**
     * 특정 레시피에 북마크를 제거한다.
     * Recipe가 ACTIVE 상태여야 하고, 북마크가 활성화된 상태여야 한다.
     */
    @Transactional
    fun unmarkRecipe(user: User, recipeId: String): BookmarkResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (!bookmarkRepository.existsByUserAndRecipe(user, recipe)) {
            throw GeneralException(ErrorStatus.LIKE_NOT_FOUND)
        }

        bookmarkRepository.deleteByUserAndRecipe(user, recipe)
        val count = bookmarkRepository.countByRecipe(recipe)

        return BookmarkResponseDto(recipe.id, count)
    }

    /**
     * 레시피의 북마크 수를 반환한다.
     */
    fun countBookmarks(recipeId: String): BookmarkResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        val count = bookmarkRepository.countByRecipe(recipe)
        return BookmarkResponseDto(recipe.id, count)
    }
}