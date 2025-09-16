package zipbap.global.global.auth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import zipbap.global.global.auth.service.TokenService
import java.io.IOException

class CustomJwtFilter(
        private val tokenService: TokenService
) : OncePerRequestFilter() {


    @Throws(ServletException::class, IOException::class)
    protected override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")
        if (header.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveBearerToken(header)

        if (token != null) {
            val authenticationToken: UsernamePasswordAuthenticationToken = tokenService.authenticationToken(token)
            SecurityContextHolder.getContext().authentication = authenticationToken
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "

        fun resolveBearerToken(input: String?): String? {
            if (input.isNullOrBlank()) return null
            val s = input.trim()
            return if (s.regionMatches(0, BEARER_PREFIX, 0, BEARER_PREFIX.length, ignoreCase = true)) {
                s.substring(BEARER_PREFIX.length).trim().ifBlank { null }
            } else {
                null
            }
        }
    }
}
