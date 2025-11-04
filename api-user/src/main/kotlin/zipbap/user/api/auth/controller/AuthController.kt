package zipbap.user.api.auth.controller

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.auth.dto.LoginRequestDto
import zipbap.user.api.auth.dto.LoginResponseDto
import zipbap.user.api.auth.service.CustomOAuth2UserService
import zipbap.user.api.auth.service.SocialLoginService
import zipbap.app.global.ApiResponse
import zipbap.global.global.auth.service.TokenService
import zipbap.global.global.code.status.ErrorStatus
// z
@RestController
@RequestMapping("/api/auth")
class AuthController(
        private val tokenService: TokenService,
        private val registrationRepository: ClientRegistrationRepository,
        private val customOAuth2UserService: CustomOAuth2UserService,
        private val socialLoginService: SocialLoginService
) {

    @GetMapping("/access-token")
    fun getAccessToken(@RequestHeader(name = "Refresh-Token", required = false) header: String?): ApiResponse<*> {
        return header?.let {token ->
            val accessToken = tokenService.reissueAccessToken(token)
            ApiResponse.onSuccess(accessToken)
        } ?: ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.code,
                ErrorStatus.INVALID_TOKEN.message, null)

    }

    @GetMapping("/test")
    fun exchangeToken(@RequestParam accessToken: String, @RequestParam registrationId: String): OAuth2User {
        val registration = registrationRepository.findByRegistrationId(registrationId)
        val tmpToken = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, null, null)
        val request = OAuth2UserRequest(registration, tmpToken, null)

        return customOAuth2UserService.loadUser(request)
    }

    @PostMapping("/{registration}/login")
    fun login(@PathVariable registration: String,
              @RequestBody dto: LoginRequestDto): ApiResponse<LoginResponseDto> {
        return ApiResponse.onSuccess(socialLoginService.loginWithSocialAccessToken(registration, dto))
    }

}
