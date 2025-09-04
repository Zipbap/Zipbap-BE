package zipbap.app.domain.category.cookingtime

import org.springframework.data.jpa.repository.JpaRepository

interface CookingTimeRepository : JpaRepository<CookingTime, Long> {
    fun existsByCookingTime(type: String): Boolean
}