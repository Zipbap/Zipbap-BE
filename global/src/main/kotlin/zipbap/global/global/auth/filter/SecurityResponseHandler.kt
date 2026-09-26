package zipbap.global.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import zipbap.app.global.ApiResponse
import zipbap.global.global.code.status.ErrorStatus

@Component
class SecurityResponseHandler(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint, AccessDeniedHandler {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        response.setHeader("WWW-Authenticate", "Bearer")
        write(response, ErrorStatus.UNAUTHORIZED)
    }

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, exception: AccessDeniedException) {
        write(response, ErrorStatus.FORBIDDEN)
    }

    fun write(response: HttpServletResponse, error: ErrorStatus) {
        if (response.isCommitted) return
        response.status = error.httpStatus.value()
        response.contentType = "application/json;charset=UTF-8"
        response.setHeader("Cache-Control", "no-store")
        objectMapper.writeValue(response.writer, ApiResponse.onFailure(error.code, error.message, null))
    }
}
