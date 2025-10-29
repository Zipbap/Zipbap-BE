package zipbap.user.auth.oauth2user.entity

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.code.status.ErrorStatus

class KakaoUser(
    oAuth2User: OAuth2User,
    clientRegistration: ClientRegistration
) : OAuth2ProviderUser(
    (oAuth2User.attributes["kakao_account"] as? Map<String, Any>?) ?: emptyMap(),
    oAuth2User,
    clientRegistration
) {
    private val kakaoAccount = (oAuth2User.attributes["kakao_account"] as? Map<String, Any>?) ?: emptyMap()
    private val profile = (kakaoAccount["profile"] as? Map<String, Any>?) ?: emptyMap()

    override val id: String = (oAuth2User.attributes["id"] ?: "").toString()

    override val username: String =
        (profile["nickname"] as? String)
            ?: throw GeneralException(ErrorStatus.SOCIAL_LOGIN_MISSING_FIELD)

    override val email: String =
        (kakaoAccount["email"] as? String)
            ?: throw GeneralException(ErrorStatus.SOCIAL_LOGIN_MISSING_FIELD)

    override fun getName(): String = username
}
