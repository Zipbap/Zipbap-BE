package zipbap.global.domain.category.headcount

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.headcount.Headcount

interface HeadcountRepository : JpaRepository<Headcount, Long> {
    fun existsByHeadcount(type: String): Boolean
}