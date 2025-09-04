package zipbap.app.domain.category.mycategory

import org.springframework.data.jpa.repository.JpaRepository

interface MyCategoryRepository : JpaRepository<MyCategory, String> {
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
    fun countByUserId(userId: Long): Long
}
