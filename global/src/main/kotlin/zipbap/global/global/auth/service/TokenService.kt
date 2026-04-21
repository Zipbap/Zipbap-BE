package zipbap.global.global.auth.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.global.global.auth.domain.token.entity.RefreshToken
import zipbap.global.global.auth.domain.token.repository.RefreshTokenRepository
import zipbap.global.global.auth.util.RefreshHmac
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.global.global.auth.JwtTokenProvider
import java.security.MessageDigest

@Service
@Transactional(readOnly = true)
class TokenService(
        private val userRepository: UserRepository,
        private val refreshTokenRepository: RefreshTokenRepository,
        private val jwtTokenProvider: JwtTokenProvider,
        private val customUserDetailsService: CustomUserDetailsService,
        private val refreshHmac: RefreshHmac
) {

    /**
     * Refresh Token 유효성 검증
     */
    fun validateRefreshToken(refreshToken: String): Boolean {
        if (!jwtTokenProvider.isRefresh(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }

        val userId: Long = jwtTokenProvider.getUserIdFromToken(refreshToken)
        val user: User = userRepository.findById(userId).orElseThrow {
            throw GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val stored = refreshTokenRepository.findByUser(user) ?: return false

        val hashedRefreshToken = refreshHmac.hmacBase64Url(refreshToken)
        return constantTimeEquals(stored.refreshToken, hashedRefreshToken)
    }

    /**
     * Access Token 유효성 검증
     */
    fun validateAccessToken(accessToken: String): Boolean {
        return jwtTokenProvider.validateToken(accessToken) && jwtTokenProvider.isAccess(accessToken)
    }

    /**
     * Access Token 생성
     */
    fun generateAccessToken(user: User): String {
        return jwtTokenProvider.generateAccessToken(user)
    }



    /**
     * Refresh Token 검증 후 Access Token 재발급
     */
    fun reissueAccessToken(token: String): String {
        require(token.isNotBlank()) { "토큰은 비어있으면 안됩니다." }

        if (!validateRefreshToken(token)) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }

        val userId: Long = jwtTokenProvider.getUserIdFromToken(token)
        val user: User = userRepository.findById(userId).orElseThrow {
            throw GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        return jwtTokenProvider.generateAccessToken(user)
    }

    /**
     * Refresh Token 생성 및 저장
     * - DB에는 해시 저장
     * - 클라이언트에는 원본 반환
     */
    @Transactional
    fun generateRefreshToken(user: User): String {
        val refreshToken: String = jwtTokenProvider.generateRefreshToken(user)

        val hashedRefreshToken = refreshHmac.hmacBase64Url(refreshToken)
        refreshTokenRepository.findByUser(user)
            ?.let { userRefresh ->
                userRefresh.updateToken(hashedRefreshToken)
                refreshTokenRepository.save(userRefresh)
            } ?: refreshTokenRepository.save(RefreshToken.createRefreshToken(user, hashedRefreshToken))

        return refreshToken
    }

    companion object {
        /**
         * 상수시간 비교
         */
        fun constantTimeEquals(a: String?, b: String?): Boolean {
            if (a == null || b == null) return false
            val aBytes = a.toByteArray(Charsets.UTF_8)
            val bBytes = b.toByteArray(Charsets.UTF_8)
            if (aBytes.size != bBytes.size) return false
            return MessageDigest.isEqual(aBytes, bBytes)
        }
    }
}
