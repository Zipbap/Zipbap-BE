package zipbap.user.api.auth.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.user.service.UserService
import zipbap.user.auth.oauth2user.entity.AppleUser
import zipbap.user.auth.oauth2user.entity.KakaoUser

@Service
class CustomOAuth2UserService(
    userService: UserService,
    private val appleIdTokenVerifier: AppleIdTokenVerifier
) : AbstractOAuth2UserService(userService), OAuth2UserService<OAuth2UserRequest, OAuth2User?> {

    private val delegate: OAuth2UserService<OAuth2UserRequest, OAuth2User> = DefaultOAuth2UserService()

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val clientRegistration = userRequest.clientRegistration
        val registrationId = clientRegistration.registrationId.lowercase()

        val providerUser: OAuth2User = when (registrationId) {
            // Kakao — 기존 로직 그대로
            "kakao" -> {
                val oAuth2User = delegate.loadUser(userRequest)
                KakaoUser(oAuth2User, clientRegistration)
            }

            // Apple — id_token 디코딩
            "apple" -> {
                val tokenValue = userRequest.accessToken.tokenValue
                val attributes = appleIdTokenVerifier.verify(tokenValue, clientRegistration.clientId)

                // Apple의 id_token에는 roles 개념이 없으므로 ROLE_USER로 임시 부여
                val fakeUser = DefaultOAuth2User(
                    listOf(SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "sub"
                )

                AppleUser(fakeUser, clientRegistration)
            }

            else -> throw GeneralException(ErrorStatus.INVALID_REGISTRATION)
        }

        // 회원 등록 (기존 로직 유지)
        super.register(providerUser as zipbap.user.auth.oauth2user.entity.ProviderUser, userRequest)

        return providerUser
    }

}
