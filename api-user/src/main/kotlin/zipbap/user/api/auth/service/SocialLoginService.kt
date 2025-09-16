package zipbap.user.api.auth.service

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.auth.oauth2user.entity.ProviderUser
import zipbap.user.api.auth.dto.LoginRequestDto
import zipbap.user.api.auth.dto.LoginResponseDto
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.auth.service.TokenService
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import java.time.Instant

@Service
@Transactional
class SocialLoginService(
        private val customOAuth2UserService: CustomOAuth2UserService,
        private val tokenService: TokenService,
        private val registrationRepository: ClientRegistrationRepository,
        private val userRepository: UserRepository
) {

    fun loginWithSocialAccessToken(
            registrationId: String,
            dto: LoginRequestDto
    ): LoginResponseDto {
        val clientRegistration = (registrationRepository.findByRegistrationId(registrationId)
                ?: throw GeneralException(ErrorStatus.INVALID_REGISTRATION))

        val now = Instant.now()
        val oAuth2AccessToken = OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                dto.accessToken,
                now.minusSeconds(60),
                now.plusSeconds(3600) // 만료시간은 임시값
        )

        val oAuth2UserRequest = OAuth2UserRequest(
                clientRegistration,
                oAuth2AccessToken)
        val loadUser = customOAuth2UserService.loadUser(oAuth2UserRequest)

        val providerUser: ProviderUser = loadUser.takeIf { it is ProviderUser }
                as? ProviderUser ?: throw GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR)
        val email: String = providerUser.email

        // email로 User 조회 후 userId 기반 토큰 발급
        val user = userRepository.findByEmail(email).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val accessToken: String = tokenService.generateAccessToken(user)
        val refreshToken: String = tokenService.generateRefreshToken(user)

        return LoginResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken
        )
    }

}