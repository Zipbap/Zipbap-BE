package zipbap.app.domain.recipe

import org.springframework.data.jpa.repository.JpaRepository

interface RecipeRepository : JpaRepository<Recipe, String> {
    fun countByUserId(userId: Long): Long
    fun findAllByUserIdAndRecipeStatus(userId: Long, status: RecipeStatus): List<Recipe>
}
