package zipbap.global.domain.recipe

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RecipeRepository : JpaRepository<Recipe, String> {

    fun countByUserId(userId: Long): Long

    fun findAllByUserIdAndRecipeStatus(
        userId: Long,
        recipeStatus: RecipeStatus
    ): List<Recipe>


    fun findTopByUserIdOrderByIdDesc(userId: Long): Recipe?

    // ✅ 여기만 남기면 됨
    fun findAllByMyCategoryId(myCategoryId: String): List<Recipe>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Recipe r SET r.viewCount = r.viewCount + 1 WHERE r.id = :recipeId
    """)
    fun updateViewCount(@Param("recipeId") recipeId: String): Unit

}
