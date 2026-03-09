package zipbap.global.global.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import zipbap.global.domain.user.User
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${spring.security.jwt.secret}")
    private val jwtSecret: String,

    @Value("\${spring.security.jwt.expiration}")
    private val jwtExpirationMs: Long,

    @Value("\${spring.security.jwt.refresh-expiration}")
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
     * 액세스 토큰 생성 (userId + email)
     */
    fun generateAccessToken(user: User): String {
        val expiryDate = Date(System.currentTimeMillis() + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(user.id.toString())
            .claim(CLAIM_EMAIL, user.email)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .claim(CLAIM_TYPE, TYPE_ACCESS)
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * 리프레시 토큰 생성 (userId만)
     */
    fun generateRefreshToken(user: User): String {
        val expiryDate = Date(System.currentTimeMillis() + refreshTokenExpirationMs)

        return Jwts.builder()
            .setSubject(user.id.toString())
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .claim(CLAIM_TYPE, TYPE_REFRESH)
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * 토큰에서 userId(Long) 추출
     */
    fun getUserIdFromToken(token: String): Long =
        getUserId(parseAndValidateToken(token))

    /**
     * @param claims        JWT에 포함된 메타데이터입니다.
     * Claim 기반 user ID 추출
     * 토큰이 유효하다면 userId는 존재한다고 가정
     */
    fun getUserId(claims: Claims): Long = claims.subject.toLong()

    /**
     * Claim 기반 email 추출
     */
    fun getEmail(claims: Claims): String? = claims.get(CLAIM_EMAIL, String::class.java)

    /**
     * 토큰 유효성 검증
     * 검증 과정에서 예외가 터지더라도 비즈니스 로직은 유지하되, 검증 실패를 전달하는거에 목적을 두고 진행해서 다시 안던짐
     */
    fun validateToken(token: String?): Boolean {
        return try {
            if (token.isNullOrBlank()) return false
            parseAndValidateToken(token)
            true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature: {}", ex.message); false
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", ex.message); false
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token: {}", ex.message); false
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token: {}", ex.message); false
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", ex.message); false
        }
    }

    /**
     * 예외 터지면 예외 잡는 필터가 책임지고 처리
     */
    fun parseAndValidateToken(token: String): Claims =
        jwtParser.parseClaimsJws(token).body


    /**
     * 토큰 타입 확인 (access/refresh)
     */
    fun getTokenType(token: String): String =
        parseAndValidateToken(token).get(CLAIM_TYPE, String::class.java)

    fun isAccess(token: String): Boolean = getTokenType(token) == TYPE_ACCESS
    fun isRefresh(token: String): Boolean = getTokenType(token) == TYPE_REFRESH


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
        private const val CLAIM_TYPE = "type"
        private const val CLAIM_EMAIL = "email"
        private const val TYPE_ACCESS = "access"
        private const val TYPE_REFRESH = "refresh"
    }
}
