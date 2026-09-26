package zipbap.user.security.lab

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import zipbap.global.domain.user.User
import zipbap.global.global.auth.domain.authuser.AuthUser
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.global.global.auth.resolver.UserInjectionArgumentResolver
import zipbap.global.global.exception.GeneralException
import zipbap.user.api.auth.service.AppleIdTokenVerifier
import zipbap.user.api.recipe.controller.RecipeTempController
import zipbap.user.api.recipe.service.RecipeService
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.Date

/** Isolated comparison fixtures, never registered by the production application. */
class SecurityLabTest {
    @Test fun `LAB 1 - identical token is parsed before and rejected after verification`() {
        fun keys() = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val trustedIssuer = keys()
        val attacker = keys()
        val decoder = NimbusJwtDecoder.withPublicKey(trustedIssuer.public as RSAPublicKey).build().also {
            it.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://appleid.apple.com"))
        }
        val verifier = AppleIdTokenVerifier(decoder)
        val email = "victim@example.invalid"
        val claims = JWTClaimsSet.Builder().issuer("https://appleid.apple.com")
            .audience("lab-client").subject("invented-subject").claim("email", email)
            .claim("email_verified", true).issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plusSeconds(300))).build()
        fun sign(key: java.security.PrivateKey) = SignedJWT(JWSHeader(JWSAlgorithm.RS256), claims)
            .apply { sign(RSASSASigner(key)) }.serialize()
        val forged = sign(attacker.private)

        // Same parse-only operation as the removed parseAppleIdToken; no verifier is called.
        val before = JWSObject.parse(forged).payload.toJSONObject()
        assertThat(before["email"]).isEqualTo(email)
        println("[LAB 1] payload email=$email, issuer=https://appleid.apple.com")
        println("[BEFORE] attacker-signed token parsed; claimed email accepted by parser=$email")
        assertThatThrownBy { verifier.verify(forged, "lab-client") }.isInstanceOf(GeneralException::class.java)
        println("[AFTER] SAME attacker-signed token rejected by AppleIdTokenVerifier")
        assertThat(verifier.verify(sign(trustedIssuer.private), "lab-client")["email"]).isEqualTo(email)
        println("[CONTROL] token signed with the trusted LOCAL issuer key accepted")
    }

    @Test fun `LAB 2 - query id changes legacy model but cannot change authenticated id`() {
        val actor = 123L
        val target = System.getProperty("lab.targetId", "456").toLong()
        require(target > 0 && target != actor) { "labTargetId must be positive and different from 123" }
        val principal = AuthUser(actor)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principal, null, principal.roles)
        try {
            val beforeService = mock(RecipeService::class.java)
            val afterService = mock(RecipeService::class.java)
            `when`(beforeService.getMyRecipesV2(target)).thenReturn(emptyList())
            `when`(afterService.getMyRecipesV2(actor)).thenReturn(emptyList())
            val before = MockMvcBuilders.standaloneSetup(LegacyController(beforeService))
                .setCustomArgumentResolvers(LegacyResolver()).build()
            val after = MockMvcBuilders.standaloneSetup(RecipeTempController(afterService))
                .setCustomArgumentResolvers(UserInjectionArgumentResolver()).build()
            fun request() = get("/api/temp/recipes").param("id", target.toString())
                .param("email", "other@example.invalid").param("nickname", "other").param("socialType", "KAKAO")
            before.perform(request()).andExpect(status().isOk)
            after.perform(request()).andExpect(status().isOk)
            verify(beforeService).getMyRecipesV2(target)
            verify(afterService).getMyRecipesV2(actor)
            verifyNoMoreInteractions(beforeService, afterService)
            println("[LAB 2] authenticated principal=$actor, query id=$target")
            println("[BEFORE] @UserInjection User unsupported -> model binding -> service id=$target")
            println("[AFTER] @UserInjection Long -> principal -> service id=$actor")
            println("[NOTE] HTTP 200 in both cases; inspect the SERVICE ARGUMENT to see the security difference")
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    // Reproduces HEAD ef33c2d's mismatched controller parameter and resolver support check.
    @RestController
    class LegacyController(private val recipes: RecipeService) {
        @GetMapping("/api/temp/recipes")
        fun list(@UserInjection user: User) = recipes.getMyRecipesV2(user.id!!)
    }

    class LegacyResolver : HandlerMethodArgumentResolver {
        override fun supportsParameter(parameter: MethodParameter) =
            parameter.hasParameterAnnotation(UserInjection::class.java) && parameter.parameterType == Long::class.java

        override fun resolveArgument(parameter: MethodParameter, container: ModelAndViewContainer?,
            request: NativeWebRequest, factory: WebDataBinderFactory?): Any? =
            (SecurityContextHolder.getContext().authentication.principal as AuthUser).userId
    }
}
