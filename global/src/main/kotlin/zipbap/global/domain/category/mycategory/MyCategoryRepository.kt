package zipbap.global.domain.category.mycategory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import zipbap.global.domain.category.mycategory.MyCategory

interface MyCategoryRepository : JpaRepository<MyCategory, String> {
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
    fun countByUserId(userId: Long): Long

    @Query(
            "SELECT COALESCE(MAX(CAST(SUBSTRING(c.id, LENGTH(CONCAT('MC-', :userId, '-')) + 1) AS long)), 0) " +
                    "FROM MyCategory c WHERE c.user.id = :userId"
    )
    fun findMaxSequenceByUserId(@Param("userId") userId: Long): Long

    fun findTopByUserIdOrderByIdDesc(userId: Long): MyCategory?

}
