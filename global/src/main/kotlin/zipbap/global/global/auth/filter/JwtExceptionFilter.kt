package zipbap.global.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.filter.OncePerRequestFilter
import zipbap.app.global.ApiResponse
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import java.io.IOException
import java.nio.file.AccessDeniedException
import java.security.SignatureException

class JwtExceptionFilter(
        private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtExceptionFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    protected override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        try {
            chain.doFilter(req, res)
        } catch (ex: Exception) {
            when (ex) {
                is ExpiredJwtException -> {
                    writeUnauthorized(res, "invalid_token", "token expired")
                    log.info("JWT expired: {}", ex.message)
                }

                is MalformedJwtException -> {
                    writeUnauthorized(res, "invalid_token", "malformed jwt")
                    log.info("JWT malformed: {}", ex.message)
                }

                is SignatureException -> {
                    writeUnauthorized(res, "invalid_token", "invalid signature")
                    log.warn("JWT bad signature: {}", ex.message)
                }

                is BadCredentialsException -> {
                    writeUnauthorized(res, "invalid_token", "bad credentials")
                    log.info("Bad credentials: {}", ex.message)
                }

                is AccessDeniedException -> {
                    writeJson(res, HttpStatus.FORBIDDEN,
                            ApiResponse.onFailure(ErrorStatus.FORBIDDEN.code, ErrorStatus.FORBIDDEN.message, null))
                    log.info("Access denied: {}", ex.message)
                }

                is GeneralException -> {
                    val status = ex.errorReasonHttpStatus.httpStatus
                    val code = ex.errorReasonHttpStatus.code
                    val msg = ex.errorReasonHttpStatus.message
                    writeJson(res, status, ApiResponse.onFailure(code, msg, null))
                    log.info("GeneralException: code={}, msg={}", code, msg)
                }

                else -> {
                    // 혹시 모를 예외 처리 (안 해주면 잡히고도 응답 안 내려갈 수 있음)
                    writeJson(res, HttpStatus.INTERNAL_SERVER_ERROR,
                            ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR.code, ErrorStatus.INTERNAL_SERVER_ERROR.message, null))
                    log.error("Unexpected exception in JwtExceptionFilter", ex)
                }
            }
        }

    }
    private fun writeUnauthorized(res: HttpServletResponse, error: String, description: String) {
        if (res.isCommitted) return
        res.status = HttpStatus.UNAUTHORIZED.value()
        // RFC 6750 권장 헤더
        res.setHeader("WWW-Authenticate", """Bearer error="$error", error_description="$description"""")
        res.contentType = "application/json;charset=UTF-8"
        val body = ApiResponse.onFailure("UNAUTHORIZED", description, null)
        res.writer.write(objectMapper.writeValueAsString(body))
    }

    private fun writeJson(res: HttpServletResponse, status: HttpStatus, body: ApiResponse<Any>) {
        if (res.isCommitted) return
        res.status = status.value()
        res.contentType = "application/json;charset=UTF-8"
        res.writer.write(objectMapper.writeValueAsString(body))
    }
}
