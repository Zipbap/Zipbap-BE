package zipbap.global.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import zipbap.global.global.code.status.ErrorStatus

class JwtExceptionFilter(objectMapper: ObjectMapper) : OncePerRequestFilter() {
    private val errors = SecurityResponseHandler(objectMapper)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            chain.doFilter(request, response)
        } catch (ex: JwtException) {
            SecurityContextHolder.clearContext()
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"")
            errors.write(response, ErrorStatus.INVALID_TOKEN)
        } catch (ex: AuthenticationException) {
            SecurityContextHolder.clearContext()
            errors.commence(request, response, ex)
        } catch (ex: AccessDeniedException) {
            errors.handle(request, response, ex)
        }
    }
}
