package zipbap.app.domain.cookingorder

import org.springframework.data.jpa.repository.JpaRepository

interface CookingOrderRepository : JpaRepository<CookingOrder, Long>
