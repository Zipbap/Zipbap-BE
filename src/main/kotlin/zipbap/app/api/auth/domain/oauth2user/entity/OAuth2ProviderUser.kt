package zipbap.app.api.auth.domain.oauth2user.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*
import java.util.stream.Collectors


abstract class OAuth2ProviderUser(
        _attributes: Map<String, Any>,

        val oAuth2User: OAuth2User,

        val clientRegistration: ClientRegistration
) : ProviderUser {

    private val attributes = _attributes

    override val password: String
        get() = UUID.randomUUID().toString()

    override val email: String
        get() = attributes["email"] as String

    override val provider: String
        get() = clientRegistration.registrationId

    override fun getAuthorities(): List<GrantedAuthority> =
            oAuth2User.authorities.map { SimpleGrantedAuthority(it.authority) }

    override fun getAttributes(): Map<String, Any> = attributes
}
