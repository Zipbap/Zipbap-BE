package zipbap.app.domain.bookmark

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import zipbap.app.domain.recipe.Recipe
import zipbap.app.domain.user.User

interface BookmarkRepository : JpaRepository<Bookmark, Long> {



    fun deleteByUserAndRecipe(user: User, recipe: Recipe)
    fun existsByUserAndRecipe(user: User, recipe: Recipe): Boolean
    fun countByRecipe(recipe: Recipe): Long
    fun countByUserId(userId: Long): Long


}