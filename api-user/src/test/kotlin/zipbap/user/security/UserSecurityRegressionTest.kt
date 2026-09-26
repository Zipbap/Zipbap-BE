package zipbap.user.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import zipbap.global.domain.user.SocialType
import zipbap.global.domain.user.User
import zipbap.global.global.auth.JwtTokenProvider
import zipbap.global.global.auth.config.AdminAccountConfig
import zipbap.global.global.auth.filter.SecurityResponseHandler
import zipbap.global.global.auth.service.AuthenticationTokenService
import zipbap.global.global.auth.service.TokenService
import zipbap.global.global.config.GlobalSecurityConfig
import zipbap.global.global.config.WebConfig
import zipbap.global.global.exception.ExceptionAdvice
import zipbap.user.api.auth.controller.AuthController
import zipbap.user.api.auth.service.SocialLoginService
import zipbap.user.api.follow.controller.FollowController
import zipbap.user.api.follow.dto.FollowResponseDto
import zipbap.user.api.follow.service.FollowService
import zipbap.user.api.recipe.controller.RecipeTempController
import zipbap.user.api.recipe.service.RecipeService
import zipbap.user.config.UserSecurityConfig
import java.util.Base64
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentCaptor
import java.util.Optional
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.cloud.service.PresignedUrlProvider
import zipbap.user.api.file.controller.FileController

@SpringJUnitConfig(UserSecurityRegressionTest.Config::class)
@WebAppConfiguration
class UserSecurityRegressionTest {
    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var tokens: JwtTokenProvider
    @Autowired lateinit var recipes: RecipeService
    @Autowired lateinit var follows: FollowService
    @Autowired lateinit var refresh: TokenService
    @Autowired lateinit var files: FileRepository
    @Autowired lateinit var users: UserRepository
    @Autowired lateinit var presigner: PresignedUrlProvider
    private lateinit var mvc: MockMvc
    private val user = User("test@example.invalid", "test", SocialType.KAKAO, id = 123L)

    @BeforeEach fun setup() {
        reset(recipes, follows, refresh, files, users, presigner)
        mvc = MockMvcBuilders.webAppContextSetup(context).apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity()).build()
        `when`(recipes.getMyRecipesV2(123L)).thenReturn(emptyList())
    }

    @Test fun `query parameters cannot change authenticated recipe owner`() {
        mvc.perform(get("/api/temp/recipes").header("Authorization", "Bearer ${tokens.generateAccessToken(user)}")
            .param("id", "456").param("email", "other@example.invalid").param("nickname", "other").param("socialType", "KAKAO"))
            .andExpect(status().isOk)
        verify(recipes).getMyRecipesV2(123L)
        verifyNoMoreInteractions(recipes)
    }

    @Test fun `follow takes actor from token rather than request model`() {
        `when`(follows.follow(123L, 456L)).thenReturn(FollowResponseDto.FollowCountDto(456L, 0, 1, true))
        mvc.perform(post("/api/follows/456").header("Authorization", "Bearer ${tokens.generateAccessToken(user)}")
            .param("id", "789").param("email", "other@example.invalid"))
            .andExpect(status().isOk)
        verify(follows).follow(123L, 456L)
    }

    @Test fun `refresh token cannot authenticate ordinary API`() {
        mvc.perform(get("/api/temp/recipes").header("Authorization", "Bearer ${tokens.generateRefreshToken(user)}"))
            .andExpect(status().isUnauthorized)
        verifyNoInteractions(recipes)
    }

    @Test fun `anonymous request is 401`() {
        mvc.perform(get("/api/temp/recipes")).andExpect(status().isUnauthorized)
    }

    @Test fun `expired malformed and wrong signature tokens are 401`() {
        val key = Base64.getEncoder().encodeToString(ByteArray(64))
        val expired = JwtTokenProvider(key, -300_000, 3_600_000).generateAccessToken(user)
        val wrongKey = JwtTokenProvider(Base64.getEncoder().encodeToString(ByteArray(64) { 1 }), 60_000, 3_600_000)
        for (token in listOf("invalid.jwt", expired, wrongKey.generateAccessToken(user))) {
            mvc.perform(get("/api/temp/recipes").header("Authorization", "Bearer $token"))
                .andExpect(status().isUnauthorized)
        }
        verifyNoInteractions(recipes)
    }

    @Test fun `debug token exchange endpoint is removed`() {
        mvc.perform(get("/api/auth/test").header("Authorization", "Bearer ${tokens.generateAccessToken(user)}"))
            .andExpect(status().isNotFound)
    }

    @Test fun `missing refresh header is 401 and cannot be cached`() {
        mvc.perform(post("/api/auth/access-token")).andExpect(status().isUnauthorized)
            .andExpect(header().string("Cache-Control", "no-store"))
    }

    @Test fun `unmatched endpoint is protected by fallback chain`() {
        mvc.perform(get("/unmatched")).andExpect(status().isUnauthorized)
    }

    @Test fun `internal exception details never reach clients`() {
        `when`(recipes.getMyRecipesV2(123L)).thenThrow(IllegalStateException("SQL secret-sentinel"))
        mvc.perform(get("/api/temp/recipes").header("Authorization", "Bearer ${tokens.generateAccessToken(user)}"))
            .andExpect(status().isInternalServerError)
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret-sentinel"))))
    }

    @Test fun `upload ownership is taken from authenticated principal`() {
        `when`(users.findById(123L)).thenReturn(Optional.of(user))
        `when`(presigner.generateUploadUrl(123L, "recipe.png")).thenReturn(mapOf(
            "uploadUrl" to "https://example.invalid/upload", "fileUrl" to "https://example.invalid/temp/123/file"))
        mvc.perform(post("/api/files/presigned-url").param("userId", "456")
            .header("Authorization", "Bearer ${tokens.generateAccessToken(user)}")
            .contentType(MediaType.APPLICATION_JSON).content("""{"fileName":"recipe.png"}"""))
            .andExpect(status().isOk)
        val saved = ArgumentCaptor.forClass(FileEntity::class.java)
        verify(files).save(saved.capture())
        assertThat(saved.value.uploader?.id).isEqualTo(123L)
        verify(presigner).generateUploadUrl(123L, "recipe.png", 5L)
    }

    @Configuration
    @EnableWebMvc
    @Import(UserSecurityConfig::class, AdminAccountConfig::class, GlobalSecurityConfig::class,
        WebConfig::class, SecurityResponseHandler::class, ExceptionAdvice::class,
        RecipeTempController::class, FollowController::class, AuthController::class, FileController::class)
    class Config {
        @Bean fun objectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()
        @Bean fun jwtTokenProvider() = JwtTokenProvider(Base64.getEncoder().encodeToString(ByteArray(64)), 60_000, 3_600_000)
        @Bean fun authenticationTokenService(provider: JwtTokenProvider) = AuthenticationTokenService(provider)
        @Bean fun recipes(): RecipeService = mock(RecipeService::class.java)
        @Bean fun follows(): FollowService = mock(FollowService::class.java)
        @Bean fun tokenService(): TokenService = mock(TokenService::class.java)
        @Bean fun socialLoginService(): SocialLoginService = mock(SocialLoginService::class.java)
        @Bean fun files(): FileRepository = mock(FileRepository::class.java)
        @Bean fun users(): UserRepository = mock(UserRepository::class.java)
        @Bean fun presigner(): PresignedUrlProvider = mock(PresignedUrlProvider::class.java)
    }
}
