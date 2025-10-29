package zipbap.user.auth.oauth2user.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

interface ProviderUser : OAuth2User {
    val id: String?
    val username: String
    val password: String?
    val email: String
    val provider: String


    override fun getAuthorities(): List<GrantedAuthority>
    override fun getAttributes(): Map<String, Any>
}
