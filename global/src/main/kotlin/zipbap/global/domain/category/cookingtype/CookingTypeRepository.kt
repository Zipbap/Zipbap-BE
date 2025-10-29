package zipbap.global.domain.category.cookingtype

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.cookingtype.CookingType

interface CookingTypeRepository : JpaRepository<CookingType, Long> {
    fun existsByType(type: String): Boolean
}