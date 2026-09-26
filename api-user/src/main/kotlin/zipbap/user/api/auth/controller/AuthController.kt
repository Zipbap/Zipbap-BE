package zipbap.user.api.auth.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import zipbap.app.global.ApiResponse
import zipbap.global.global.auth.service.TokenService
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.auth.dto.LoginRequestDto
import zipbap.user.api.auth.dto.LoginResponseDto
import zipbap.user.api.auth.service.SocialLoginService

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val tokenService: TokenService,
    private val socialLoginService: SocialLoginService
) {
    // GET remains compatible with existing clients; POST is preferred.
    @RequestMapping("/access-token", method = [RequestMethod.GET, RequestMethod.POST])
    fun getAccessToken(@RequestHeader(name = "Refresh-Token", required = false) header: String?,
                       response: HttpServletResponse): ApiResponse<*> {
        response.setHeader("Cache-Control", "no-store")
        if (header.isNullOrBlank()) throw GeneralException(ErrorStatus.INVALID_TOKEN)
        return ApiResponse.onSuccess(tokenService.reissueAccessToken(header))
    }

    @PostMapping("/{registration}/login")
    fun login(@PathVariable registration: String, @RequestBody dto: LoginRequestDto,
              response: HttpServletResponse): ApiResponse<LoginResponseDto> {
        response.setHeader("Cache-Control", "no-store")
        return ApiResponse.onSuccess(socialLoginService.loginWithSocialAccessToken(registration, dto))
    }
}
