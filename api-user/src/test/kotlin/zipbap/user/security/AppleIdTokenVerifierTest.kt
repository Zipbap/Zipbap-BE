package zipbap.user.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.SignedJWT
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.auth.service.AppleIdTokenVerifier
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.Date

class AppleIdTokenVerifierTest {
    private val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    private val decoder = NimbusJwtDecoder.withPublicKey(keys.public as RSAPublicKey).build().also {
        it.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://appleid.apple.com"))
    }
    private val verifier = AppleIdTokenVerifier(decoder)

    private fun token(kind: String = "valid"): String {
        val claims = JWTClaimsSet.Builder().issuer("https://appleid.apple.com").audience("test-client")
            .subject("apple-user").issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plusSeconds(300)))
            .claim("email", "test@example.invalid").claim("email_verified", true)
        when (kind) {
            "expired" -> claims.expirationTime(Date.from(Instant.now().minusSeconds(300)))
            "issuer" -> claims.issuer("https://attacker.invalid")
            "audience" -> claims.audience("different-client")
            "missing-expiry" -> claims.expirationTime(null)
            "missing-subject" -> claims.subject(null)
            "email" -> claims.claim("email_verified", false)
            "malformed" -> return "invalid.jwt"
        }
        if (kind == "unsigned") return PlainJWT(claims.build()).serialize()
        val signingKey = if (kind == "signature") KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair().private else keys.private
        return SignedJWT(JWSHeader(JWSAlgorithm.RS256), claims.build()).apply { sign(RSASSASigner(signingKey)) }.serialize()
    }

    @Test fun `valid signed Apple identity is accepted`() {
        assertThat(verifier.verify(token(), "test-client")["sub"]).isEqualTo("apple-user")
    }

    @ParameterizedTest
    @ValueSource(strings = ["expired", "issuer", "audience", "missing-expiry", "missing-subject", "email", "malformed", "unsigned", "signature"])
    fun `invalid identity is rejected`(kind: String) {
        assertThatThrownBy { verifier.verify(token(kind), "test-client") }
            .isInstanceOfSatisfying(GeneralException::class.java) {
                assertThat(it.errorReasonHttpStatus.code).isEqualTo(ErrorStatus.INVALID_TOKEN.code)
            }
    }
}
