package zipbap.app.api.auth.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.domain.token.service.TokenService
import zipbap.app.api.auth.dto.LoginRequestDto
import zipbap.app.api.auth.dto.LoginResponseDto
import zipbap.app.api.auth.service.CustomOAuth2UserService
import zipbap.app.api.auth.service.SocialLoginService
import zipbap.app.global.ApiResponse
import zipbap.app.global.code.status.ErrorStatus

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

    @GetMapping("/{registration}/login")
    fun login(@PathVariable registration: String,
              @RequestBody dto: LoginRequestDto): ApiResponse<LoginResponseDto> {
        return ApiResponse.onSuccess(socialLoginService.loginWithSocialAccessToken(registration, dto))
    }

}
