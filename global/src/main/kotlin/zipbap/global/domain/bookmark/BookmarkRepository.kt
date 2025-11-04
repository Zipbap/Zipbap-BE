package zipbap.global.domain.bookmark

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.User

interface BookmarkRepository : JpaRepository<Bookmark, Long> {



    fun deleteByUserAndRecipe(user: User, recipe: Recipe)
    fun existsByUserAndRecipe(user: User, recipe: Recipe): Boolean
    fun countByRecipe(recipe: Recipe): Long
    fun countByUserId(userId: Long): Long

    @Query("SELECT b FROM Bookmark b JOIN FETCH b.recipe " +
            "WHERE b.user = :user AND b.recipe.recipeStatus = :recipeStatus ")
    fun findByUser(user: User, recipeStatus: RecipeStatus): List<Bookmark>

    @Query(
            "SELECT COALESCE(MAX(CAST(SUBSTRING(b.bookmarkId, LENGTH(CONCAT('BM-', :userId, '-')) + 1) AS long)), 0) " +
                    "FROM Bookmark b WHERE b.user.id = :userId"
    )
    fun findMaxSequenceByUserId(@Param("userId") userId: Long): Long

}