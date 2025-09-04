package zipbap.app.domain.category.mainingredient

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.app.domain.category.cookingtype.CookingType

interface MainIngredientRepository : JpaRepository<MainIngredient, Long> {
    fun existsByIngredient(type: String): Boolean
}