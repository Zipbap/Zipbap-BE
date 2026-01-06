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
    """)
    fun findAllFeed(
        @Param("userId") userId: Long,
        @Param("recipeStatus") recipeStatus: RecipeStatus,
        @Param("isPrivate") isPrivate: Boolean
    ): List<Recipe>

    fun findTopByUser_IdOrderByIdDesc(userId: Long): Recipe?

    // ✅ 여기만 남기면 됨
    fun findAllByMyCategoryId(myCategoryId: String): List<Recipe>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Recipe r SET r.viewCount = r.viewCount + 1 WHERE r.id = :recipeId
    """)
    fun updateViewCount(@Param("recipeId") recipeId: String): Unit

    @Query("""
          select r from Recipe r
            join fetch r.user u
            left join fetch r.myCategory mc
            left join fetch r.cookingType ct
            left join fetch r.situation s
            left join fetch r.mainIngredient mi
            left join fetch r.method m
            left join fetch r.headcount hc
            left join fetch r.cookingTime ctime
            left join fetch r.level lv
          where r.id = :recipeId
    """)
    fun findByIdForDetail(@Param("recipeId") recipeId: String): Recipe?
}
