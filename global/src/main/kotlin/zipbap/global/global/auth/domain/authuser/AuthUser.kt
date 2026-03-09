package zipbap.global.global.auth.domain.authuser

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class AuthUser(
        val userId: Long,
        val roles: List<SimpleGrantedAuthority>? = listOf(SimpleGrantedAuthority("ROLE_USER"))
) {
}