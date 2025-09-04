package zipbap.app.domain.category.headcount

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.app.domain.category.cookingtype.CookingType
import zipbap.app.domain.category.mainingredient.MainIngredient

interface HeadcountRepository : JpaRepository<Headcount, Long> {
    fun existsByHeadcount(type: String): Boolean
}