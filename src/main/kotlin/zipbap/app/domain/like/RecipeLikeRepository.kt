package zipbap.app.domain.like

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.app.domain.recipe.Recipe
import zipbap.app.domain.user.User

interface RecipeLikeRepository : JpaRepository<RecipeLike, Long> {
    fun existsByUserAndRecipe(user: User, recipe: Recipe): Boolean
    fun deleteByUserAndRecipe(user: User, recipe: Recipe)
    fun countByRecipe(recipe: Recipe): Long
}
