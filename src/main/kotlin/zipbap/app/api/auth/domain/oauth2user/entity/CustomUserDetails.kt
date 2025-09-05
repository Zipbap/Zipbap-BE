package zipbap.app.api.auth.domain.oauth2user.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import zipbap.app.domain.user.User
import java.util.List

class CustomUserDetails(
        val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return List.of(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return user.nickname
    }
}
