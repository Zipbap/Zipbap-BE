package zipbap.user.auth.oauth2user.entity

import com.nimbusds.jose.JWSObject
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import java.util.*

class AppleUser(
    oAuth2User: OAuth2User,
    clientRegistration: ClientRegistration
) : OAuth2ProviderUser(
    oAuth2User.attributes,
    oAuth2User,
    clientRegistration
) {
    override val id: String
        get() = getAttributes()["sub"] as? String
            ?: throw GeneralException(ErrorStatus.SOCIAL_LOGIN_MISSING_FIELD)

    override val email: String
        get() = getAttributes()["email"] as? String
            ?: throw GeneralException(ErrorStatus.SOCIAL_LOGIN_MISSING_FIELD)

    override val username: String
        get() = email.substringBefore("@")

    override fun getName(): String = username

    companion object {
        /**
         * Apple의 id_token (JWT)을 디코드해서 claims를 반환
         */
        fun parseClaims(idToken: String): Map<String, Any> {
            val jwsObject = JWSObject.parse(idToken)
            return jwsObject.payload.toJSONObject()
        }
    }
}
