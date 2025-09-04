package zipbap.app.domain.category.method

import org.springframework.data.jpa.repository.JpaRepository

interface MethodRepository : JpaRepository<Method, Long> {
    fun existsByMethod(type: String): Boolean
}