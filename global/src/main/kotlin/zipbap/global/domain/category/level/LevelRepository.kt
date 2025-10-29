package zipbap.global.domain.category.level

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.level.Level

interface LevelRepository : JpaRepository<Level, Long> {
    fun existsByLevel(type: String): Boolean
}