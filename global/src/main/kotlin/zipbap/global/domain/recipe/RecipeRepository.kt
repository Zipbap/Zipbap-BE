package zipbap.global.domain.recipe

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

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

    @Query("""
       SELECT r from Recipe r WHERE r.user.id = :userId
        AND r.recipeStatus = :recipeStatus
        AND r.isPrivate = :isPrivate
        ORDER BY r.createdAt
    """
    )
    fun findAllFeed(@Param("userId") userId: Long, @Param("recipeStatus") recipeStatus: RecipeStatus,
                    @Param("isPrivate") isPrivate: Boolean): List<Recipe>
}
