package zipbap.user.api.boomark.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.boomark.converter.BookmarkConverter
import zipbap.user.api.boomark.dto.BookmarkResponseDto
import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.bookmark.BookmarkRepository
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.util.CustomIdGenerator

@Service
@Transactional(readOnly = true)
class BookmarkService(
        private val bookmarkRepository: BookmarkRepository,
        private val recipeRepository: RecipeRepository,
        private val userRepository: UserRepository
) {

    /**
     * 특정 레시피에 북마크를 추가한다.
     * Recipe가 ACTIVE 상태여야 하고, 이전에 북마크한 기록이 있으면 안된다.
     */
    @Transactional
    fun markRecipe(userId: Long, recipeId: String
    ): BookmarkResponseDto.BookmarkSimpleResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }
        val userRef = userRepository.getReferenceById(userId)

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (bookmarkRepository.existsByUserAndRecipe(userRef, recipe)) {
            throw GeneralException(ErrorStatus.ALREADY_BOOKMARK_RECIPE)
        }

        val tmp = bookmarkRepository.findTopByUserIdOrderByIdDesc(userRef.id!!)
        val seq = tmp?.id?.split("-")?.lastOrNull()?.toLongOrNull() ?: 0L
        val generatedId = CustomIdGenerator.generate("BM", userRef.id!!, seq + 1)


        bookmarkRepository.save(Bookmark(userRef, recipe, generatedId))
        val count = bookmarkRepository.countByRecipe(recipe)

        return BookmarkConverter.toSimpleDto(recipe.id, count)
    }

    /**
     * 특정 레시피에 북마크를 제거한다.
     * Recipe가 ACTIVE 상태여야 하고, 북마크가 활성화된 상태여야 한다.
     */
    @Transactional
    fun unmarkRecipe(userId: Long, recipeId: String
    ): BookmarkResponseDto.BookmarkSimpleResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }
        val userRef = userRepository.getReferenceById(userId)

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        if (!bookmarkRepository.existsByUserAndRecipe(userRef, recipe)) {
            throw GeneralException(ErrorStatus.BOOKMARK_NOT_FOUND)
        }

        bookmarkRepository.deleteByUserAndRecipe(userRef, recipe)
        val count = bookmarkRepository.countByRecipe(recipe)

        return BookmarkConverter.toSimpleDto(recipe.id, count)
    }

    /**
     * 레시피의 북마크 수를 반환한다.
     */
    fun countBookmarks(recipeId: String): BookmarkResponseDto.BookmarkSimpleResponseDto {
        val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { GeneralException(ErrorStatus.RECIPE_NOT_FOUND) }

        if (recipe.recipeStatus != RecipeStatus.ACTIVE) {
            throw GeneralException(ErrorStatus.RECIPE_FORBIDDEN)
        }

        val count = bookmarkRepository.countByRecipe(recipe)

        return BookmarkConverter.toSimpleDto(recipe.id, count)
    }

    fun getMarkedRecipe(userId: Long): List<BookmarkResponseDto.BookmarkRecipeResponseDto> {
        val recipes = bookmarkRepository.findByUser(userId, RecipeStatus.ACTIVE)
                .map {
                    BookmarkConverter.toThumbnailDto(it.recipe)
                } .toList()

        return recipes

    }
}