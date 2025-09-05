package zipbap.app.api.auth.handler

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import zipbap.app.api.auth.domain.oauth2user.entity.ProviderUser
import zipbap.app.api.auth.domain.token.service.TokenResponseWriter
import zipbap.app.api.auth.domain.token.service.TokenService
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException
import java.io.IOException

/*
   OAuth2 인증이 성공하면, 이를 바탕으로 JWT Token을 생성합니다.
*/
@Component
class CustomSuccessHandler(
    private val tokenService: TokenService,
    private val tokenResponseWriter: TokenResponseWriter,
    private val userRepository: UserRepository
) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal: ProviderUser = authentication.principal.takeIf { it is ProviderUser }
                as? ProviderUser ?: throw GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR)

        val email: String = principal.email
            ?: throw GeneralException(ErrorStatus.FORBIDDEN)

        // email로 User 조회 후 userId 기반 토큰 발급
        val user = userRepository.findByEmail(email).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val accessToken: String = tokenService.generateAccessToken(user)
        val refreshToken: String = tokenService.generateRefreshToken(user)

        tokenResponseWriter.write(response, accessToken, refreshToken)
        clearAuthenticationAttributes(request)
    }

    private fun clearAuthenticationAttributes(request: HttpServletRequest) {
        val session = request.getSession(false) ?: return
        session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION")
        // 필요 시 SavedRequest, OAuth2AuthorizationRequest 쿠키/세션 정리 로직 추가
    }
}
