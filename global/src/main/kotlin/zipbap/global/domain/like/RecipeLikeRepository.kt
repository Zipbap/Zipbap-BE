package zipbap.global.domain.like

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

interface RecipeLikeRepository : JpaRepository<RecipeLike, Long> {
    fun existsByUserAndRecipe(user: User, recipe: Recipe): Boolean
    fun deleteByUserAndRecipe(user: User, recipe: Recipe)
    fun countByRecipe(recipe: Recipe): Long

    @Query("SELECT l.recipe.id FROM RecipeLike l WHERE l.user.id = :userId AND l.recipe.id IN :recipeIds")
    fun findRecipeIdsByUserIdAndRecipeIdIn(
        @Param("userId") userId: Long,
        @Param("recipeIds") recipeIds: Collection<String>
    ): List<String>
}
