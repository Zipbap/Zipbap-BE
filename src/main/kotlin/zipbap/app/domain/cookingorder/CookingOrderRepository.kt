package zipbap.app.domain.cookingorder

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CookingOrderRepository : JpaRepository<CookingOrder, Long> {

    /**
     * 특정 레시피에 속한 모든 조리 순서 물리 삭제 (배치 JPQL)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CookingOrder co where co.recipe.id = :recipeId")
    fun deleteAllByRecipeId(@Param("recipeId") recipeId: String): Int

    /**
     * 특정 레시피에 속한 모든 조리 순서 조회
     */
    fun findAllByRecipeId(recipeId: String): List<CookingOrder>
}
