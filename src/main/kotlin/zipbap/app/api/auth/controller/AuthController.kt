package zipbap.app.api.auth.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.domain.token.service.TokenService
import zipbap.app.global.ApiResponse
import zipbap.app.global.code.status.ErrorStatus

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val tokenService: TokenService
) {

    @GetMapping("/access-token")
    fun getAccessToken(@RequestHeader(name = "Refresh-Token", required = false) header: String?): ApiResponse<*> {
        return header?.let {token ->
            val accessToken = tokenService.reissueAccessToken(token)
            ApiResponse.onSuccess(accessToken)
        } ?: ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.code,
                ErrorStatus.INVALID_TOKEN.message, null)

    }
}
