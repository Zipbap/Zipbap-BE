package zipbap.app.api.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import zipbap.app.global.ApiResponse
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException
import java.io.IOException

@Component
class CustomFailureHandler(
    private val objectMapper: ObjectMapper
) : SimpleUrlAuthenticationFailureHandler() {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @Throws(IOException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse,
                                         exception: AuthenticationException) {
        if (response.isCommitted) return
        // 응답 상태 설정
        response.status = HttpServletResponse.SC_UNAUTHORIZED // 401
        response.contentType = "application/json;charset=UTF-8"

        val (code, message) = when (exception) {
            is OAuth2AuthenticationException ->
                ErrorStatus.OAUTH2_LOGIN_FAIL.code to ErrorStatus.OAUTH2_LOGIN_FAIL.message
            else ->
                ErrorStatus.AUTHENTICATION_FAILED.code to (exception.message ?: "인증에 실패했습니다.")
        }

        val wwwAuth = """Bearer error="oauth2_login_fail", error_description="OAuth2 Login failed""""
        response.setHeader("WWW-Authenticate", wwwAuth)
//        log.info(exception.message)

        // 에러 메시지 구성
        val apiResponse: ApiResponse<Any> = ApiResponse.onFailure(code, message, null)


        // JSON으로 응답
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(jsonResponse)

        log.info("Authentication failed: {} - {}", exception.javaClass.simpleName, exception.message)
    }
}
