package zipbap.app.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 존재 여부 확인
     */
    fun existsByEmail(email: String): Boolean

    /**
     * 이메일로 사용자 조회
     */
    fun findByEmail(email: String): Optional<User>
}
