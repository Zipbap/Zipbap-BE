package zipbap.app.domain.category.level

import org.springframework.data.jpa.repository.JpaRepository

interface LevelRepository : JpaRepository<Level, Long> {
    fun existsByLevel(type: String): Boolean
}