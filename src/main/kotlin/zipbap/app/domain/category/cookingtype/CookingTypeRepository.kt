package zipbap.app.domain.category.cookingtype

import org.springframework.data.jpa.repository.JpaRepository

interface CookingTypeRepository : JpaRepository<CookingType, Long> {
    fun existsByType(type: String): Boolean
}