package zipbap.global.domain.like

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.like.RecipeLike
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

interface RecipeLikeRepository : JpaRepository<RecipeLike, Long> {
    fun existsByUserAndRecipe(user: User, recipe: Recipe): Boolean
    fun deleteByUserAndRecipe(user: User, recipe: Recipe)
    fun countByRecipe(recipe: Recipe): Long
}
