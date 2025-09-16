package zipbap.global.domain.category.mycategory

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.mycategory.MyCategory

interface MyCategoryRepository : JpaRepository<MyCategory, String> {
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
    fun countByUserId(userId: Long): Long
}
