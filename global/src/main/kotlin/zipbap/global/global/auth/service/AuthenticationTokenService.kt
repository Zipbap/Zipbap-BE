package zipbap.global.global.auth.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.security.authentication.BadCredentialsException
import zipbap.global.global.auth.JwtTokenProvider
import zipbap.global.global.auth.domain.authuser.AuthUser

@Service
class AuthenticationTokenService(
        private val jwtTokenProvider: JwtTokenProvider
) {
    /**
     * token에 대한 유효성 검사 + 통과시 인증용 객체 생성
     */
    fun authenticateToken(token: String): UsernamePasswordAuthenticationToken {
        if (token.isBlank()) throw BadCredentialsException("Missing access token")

        val claims = jwtTokenProvider.parseAndValidateToken(token)
        if (claims["type"] != "access") throw BadCredentialsException("Access token required")
        val userId = claims.subject?.toLongOrNull()?.takeIf { it > 0 }
            ?: throw BadCredentialsException("Invalid token subject")

        val authUser = AuthUser(userId)


        return UsernamePasswordAuthenticationToken(authUser, null, authUser.roles)
    }
}
