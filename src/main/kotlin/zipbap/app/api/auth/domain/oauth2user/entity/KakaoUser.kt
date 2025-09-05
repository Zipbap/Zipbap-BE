package zipbap.app.api.auth.domain.oauth2user.entity

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User

class KakaoUser(
        oAuth2User: OAuth2User,
        clientRegistration: ClientRegistration
) : OAuth2ProviderUser(oAuth2User.attributes["kakao_account"] as Map<String, Any>,
        oAuth2User, clientRegistration) {
    override val id: String?
        get() = null

    override val username: String
        get() = (attributes["profile"] as Map<String, String>)["nickname"]!!


    override fun getName(): String {
        return username
    }
}
