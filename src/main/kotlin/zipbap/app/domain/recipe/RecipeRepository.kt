package zipbap.app.domain.recipe

import org.springframework.data.jpa.repository.JpaRepository

interface RecipeRepository : JpaRepository<Recipe, String> {

    fun countByUserId(userId: Long): Long

    fun findAllByUserIdAndRecipeStatus(
        userId: Long,
        recipeStatus: RecipeStatus
    ): List<Recipe>

    fun findAllByUserIdAndRecipeStatusAndMyCategoryIdIn(
        userId: Long,
        recipeStatus: RecipeStatus,
        myCategoryIds: Collection<String>
    ): List<Recipe>
}
