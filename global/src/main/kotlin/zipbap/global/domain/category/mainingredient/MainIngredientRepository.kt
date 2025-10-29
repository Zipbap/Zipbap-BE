package zipbap.global.domain.category.mainingredient

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.mainingredient.MainIngredient

interface MainIngredientRepository : JpaRepository<MainIngredient, Long> {
    fun existsByIngredient(type: String): Boolean
}