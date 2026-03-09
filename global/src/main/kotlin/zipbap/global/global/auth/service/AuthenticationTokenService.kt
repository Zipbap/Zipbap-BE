package zipbap.global.global.auth.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
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
        require(token.isNotBlank()) { "토큰은 비어있으면 안됩니다." }

        val claims = jwtTokenProvider.parseAndValidateToken(token)
        val userId: Long = jwtTokenProvider.getUserId(claims) // 토큰이 유효하다면 userId는 존재한다고 가정

        val authUser = AuthUser(userId)


        return UsernamePasswordAuthenticationToken(authUser, null, authUser.roles)
    }
}