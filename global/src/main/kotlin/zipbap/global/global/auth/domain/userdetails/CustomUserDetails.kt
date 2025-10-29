package zipbap.global.global.auth.domain.userdetails

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import zipbap.global.domain.user.User

class CustomUserDetails(
        val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return user.nickname
    }
}
