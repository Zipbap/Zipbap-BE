package zipbap.app.api.auth.domain.token.repository

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.app.api.auth.domain.token.entity.RefreshToken
import zipbap.app.domain.user.User
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken?, Long?> {
    fun findByUser(user: User): RefreshToken?
}
