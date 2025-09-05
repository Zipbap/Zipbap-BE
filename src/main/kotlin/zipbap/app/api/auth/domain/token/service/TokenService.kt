package zipbap.app.api.auth.domain.token.service

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.auth.JwtTokenProvider
import zipbap.app.api.auth.domain.token.entity.RefreshToken
import zipbap.app.api.auth.domain.token.repository.RefreshTokenRepository
import zipbap.app.api.auth.domain.token.util.RefreshHmac
import zipbap.app.api.auth.service.CustomUserDetailsService
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException
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
     * validate 전에 Bearer 파싱이 되어서 들어온다고 가정한다.
     */
    fun validateRefreshToken(refreshToken: String): Boolean {
        if (!jwtTokenProvider.isRefresh(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }

        val email: String = jwtTokenProvider.getUsernameFromToken(refreshToken)
        val user: User = userRepository.findByEmail(email)
                ?: throw GeneralException(ErrorStatus.USER_NOT_FOUND)

        val stored = refreshTokenRepository.findByUser(user)
                ?: return false

        val hashedRefreshToken = refreshHmac.hmacBase64Url(refreshToken)
        return constantTimeEquals(stored.refreshToken, hashedRefreshToken)
    }

    fun validateAccessToken(accessToken: String): Boolean {
        return jwtTokenProvider.validateToken(accessToken) && jwtTokenProvider.isAccess(accessToken)
    }

    fun generateAccessToken(email: String): String {
        require(email.isNotBlank()) { "이메일은 비어있으면 안됩니다. "}

        return jwtTokenProvider.generateAccessToken(email)
    }

    fun authenticationToken(token: String): UsernamePasswordAuthenticationToken {
        require(token.isNotBlank()) { "토큰은 비어있으면 안됩니다. "}

        if (!validateAccessToken(token)) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }

        val email: String = jwtTokenProvider.getUsernameFromToken(token)
        val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(email)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
    }


    fun reissueAccessToken(token: String): String {
        require(token.isNotBlank()) { "토큰은 비어있으면 안됩니다. "}


        if (!validateRefreshToken(token)) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }
        val email = jwtTokenProvider.getUsernameFromToken(token)
        return jwtTokenProvider.generateAccessToken(email)
    }

    /**
     * RefreshToken을 생성하는 메소드입니다.
     * DB에는 토큰을 Hash로 저장, return은 원본
     */
    @Transactional
    fun generateRefreshToken(email: String): String {
        require(email.isNotBlank()) { "이메일은 비어있으면 안됩니다. "}

        val refreshToken: String = jwtTokenProvider.generateRefreshToken(email)
        val user: User = userRepository.findByEmail(email)
                ?: throw GeneralException(ErrorStatus.USER_NOT_FOUND)


        val hashedRefreshToken = refreshHmac.hmacBase64Url(refreshToken)
        refreshTokenRepository.findByUser(user)
                ?. let {userRefresh ->
                    userRefresh.updateToken(hashedRefreshToken)
                    refreshTokenRepository.save(userRefresh)
                } ?: refreshTokenRepository.save(RefreshToken.createRefreshToken(user, hashedRefreshToken))

        return refreshToken
    }

    companion object {

        /**
         * 상수시간 비교(길이가 다르면 false, 길이가 같으면 바이트 단위 XOR 누적)
         * JDK 내장: MessageDigest.isEqual(a, b) 사용도 가능
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
