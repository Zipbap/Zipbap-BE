package zipbap.user.auth.oauth2user.entity

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * 임시로 NaverUser -> AppleUser 구현해논 상태입니다.
 * 나중에 Apple에 맞게 변경하겠습니다.
 */
class AppleUser(oAuth2User: OAuth2User,
                clientRegistration: ClientRegistration
) : OAuth2ProviderUser(oAuth2User.attributes["response"] as Map<String, Any>,
        oAuth2User, clientRegistration) {
    override val id: String?
        get() = getAttributes()["id"] as String?

    override val username: String
        get() = getAttributes()["name"] as String

    override fun getName(): String {
        return username
    }
}