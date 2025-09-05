package zipbap.app.api.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import zipbap.app.api.auth.service.CustomUserDetailsService
import java.util.*
import javax.crypto.SecretKey

// made by claude
@Component
class JwtTokenProvider(
        @Value("\${spring.security.jwt.secret}")
    private val jwtSecret: String,

        @Value("\${spring.security.jwt.expiration}") // 24시간
    private val jwtExpirationMs: Long,

        @Value("\${spring.security.jwt.refresh-expiration}") // 7일
    private val refreshTokenExpirationMs: Long
) {


    private val signingKey: SecretKey by lazy {
        val decoded = try {
            Base64.getUrlDecoder().decode(jwtSecret)
        } catch (_: IllegalArgumentException) {
            Base64.getDecoder().decode(jwtSecret)
        }
        Keys.hmacShaKeyFor(decoded)
    }

    private val jwtParser: JwtParser by lazy {
        Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(60)
                .build()
    }

    /**
     * 액세스 토큰 생성
     */
    fun generateAccessToken(authentication: Authentication): String {
        val userPrincipal: UserDetails = authentication.principal as UserDetails
        return generateAccessToken(userPrincipal.getUsername())
    }

    fun generateAccessToken(email: String): String {
        val expiryDate = Date(System.currentTimeMillis() + jwtExpirationMs)

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .claim("type", "access")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact()
    }

    /**
     * 리프레시 토큰 생성
     */
    fun generateRefreshToken(email: String): String {
        val expiryDate = Date(System.currentTimeMillis() + refreshTokenExpirationMs)

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact()
    }

    /**
     * 토큰에서 사용자명 추출
     */
    fun getUsernameFromToken(token: String): String =
            parseClaims(token).subject

    /**
     * 토큰 유효성 검증
     */
    fun validateToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
            true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature: {}", ex.message)
            false
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", ex.message)
            false
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token: {}", ex.message)
            false
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token: {}", ex.message)
            false
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", ex.message)
            false
        }
    }

    /**
     * 토큰 타입 확인 (access/refresh)
     */
    fun getTokenType(token: String): String =
            parseClaims(token).get("type", String::class.java)


    /**
     * 토큰 만료시간 확인
     */
    fun getExpirationDateFromToken(token: String): Date =
            parseClaims(token).expiration

    fun isAccess(token: String): Boolean {
        return getTokenType(token) == "access"
    }

    fun isRefresh(token: String): Boolean {
        return getTokenType(token) == "refresh"
    }

    private fun parseClaims(token: String): Claims =
            jwtParser.parseClaimsJws(token).body

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    }
}
