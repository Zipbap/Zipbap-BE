package zipbap.app.api.auth.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import zipbap.app.api.user.service.UserService

@Service
class CustomOAuth2UserService(
    userService: UserService
) : AbstractOAuth2UserService(userService), OAuth2UserService<OAuth2UserRequest, OAuth2User?> {

    private val delegate: OAuth2UserService<OAuth2UserRequest, OAuth2User> = DefaultOAuth2UserService()
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val clientRegistration = userRequest.clientRegistration

        val oAuth2User = delegate.loadUser(userRequest)

        val providerUser = super.providerUser(clientRegistration, oAuth2User)
                ?: throw OAuth2AuthenticationException("유저 생성에 실패하였습니다.")

        super.register(providerUser, userRequest)

        return providerUser
    }
}
