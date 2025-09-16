package zipbap.global.domain.category.method

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.domain.category.method.Method

interface MethodRepository : JpaRepository<Method, Long> {
    fun existsByMethod(type: String): Boolean
}