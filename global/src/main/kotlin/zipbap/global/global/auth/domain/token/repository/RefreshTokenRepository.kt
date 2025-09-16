package zipbap.global.global.auth.domain.token.repository

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.global.global.auth.domain.token.entity.RefreshToken
import zipbap.global.domain.user.User

interface RefreshTokenRepository : JpaRepository<RefreshToken?, Long?> {
    fun findByUser(user: User): RefreshToken?
}
