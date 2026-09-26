package zipbap.user.api.auth.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Configuration
class AppleTokenConfig {
    @Bean
    fun appleIdTokenDecoder(): JwtDecoder = NimbusJwtDecoder
        .withJwkSetUri("https://appleid.apple.com/auth/keys")
        .jwsAlgorithm(SignatureAlgorithm.RS256).build().also {
            it.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://appleid.apple.com"))
        }
}

@Component
class AppleIdTokenVerifier(@Qualifier("appleIdTokenDecoder") private val decoder: JwtDecoder) {
    fun verify(token: String, clientId: String): Map<String, Any> {
        val jwt = try {
            decoder.decode(token)
        } catch (ex: JwtException) {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }
        if (clientId.isBlank() || clientId !in jwt.audience || jwt.expiresAt == null ||
            jwt.subject.isNullOrBlank() || jwt.getClaimAsString("email").isNullOrBlank() ||
            jwt.claims["email_verified"].toString() != "true") {
            throw GeneralException(ErrorStatus.INVALID_TOKEN)
        }
        return jwt.claims
    }
}
