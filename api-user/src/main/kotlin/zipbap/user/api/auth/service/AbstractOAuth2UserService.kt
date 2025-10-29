package zipbap.user.api.auth.service

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.auth.oauth2user.entity.KakaoUser
import zipbap.user.auth.oauth2user.entity.AppleUser
import zipbap.user.auth.oauth2user.entity.ProviderUser
import zipbap.user.api.user.service.UserService

@Service
abstract class AbstractOAuth2UserService(
        private val userService: UserService,
) {

    @Transactional
    fun register(providerUser: ProviderUser, userRequest: OAuth2UserRequest) {
        if (userService.isUserExists(providerUser.email)) return

        val clientRegistration: ClientRegistration = userRequest.clientRegistration
        userService.register(clientRegistration.registrationId, providerUser.username, providerUser.email)
    }

    fun providerUser(clientRegistration: ClientRegistration, oAuth2User: OAuth2User): ProviderUser? {
        val registrationId: String = clientRegistration.registrationId

        return when (registrationId.uppercase()) {
            "APPLE" -> AppleUser(oAuth2User, clientRegistration) // APPLE 유저는 임시용
            "KAKAO" -> KakaoUser(oAuth2User, clientRegistration)
            else -> null
        }
    }
}
