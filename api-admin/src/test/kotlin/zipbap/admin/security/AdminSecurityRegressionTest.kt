package zipbap.admin.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import zipbap.admin.config.SecurityConfig
import zipbap.global.global.auth.JwtTokenProvider
import zipbap.global.global.auth.config.AdminAccountConfig
import zipbap.global.global.auth.filter.SecurityResponseHandler
import zipbap.global.global.auth.service.AuthenticationTokenService
import zipbap.global.global.config.GlobalSecurityConfig
import java.util.Base64

@SpringJUnitConfig(AdminSecurityRegressionTest.Config::class)
@WebAppConfiguration
@TestPropertySource(properties = ["spring.security.user.name=test-admin", "spring.security.user.password={noop}test-only-password"])
class AdminSecurityRegressionTest {
    @Autowired lateinit var context: WebApplicationContext
    private lateinit var mvc: MockMvc

    @BeforeEach fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity()).build()
    }

    @Test fun `anonymous cannot read management endpoints`() {
        mvc.perform(get("/admin/review-probe")).andExpect(status().isUnauthorized)
    }

    @Test fun `ordinary user cannot read or write even with CSRF`() {
        mvc.perform(get("/admin/review-probe").with(user("member").roles("USER"))).andExpect(status().isForbidden)
        mvc.perform(post("/admin/review-probe").with(user("member").roles("USER")).with(csrf()))
            .andExpect(status().isForbidden)
    }

    @Test fun `administrator can read and write with CSRF`() {
        mvc.perform(get("/admin/review-probe").with(user("admin").roles("ADMIN"))).andExpect(status().isOk)
        mvc.perform(post("/admin/review-probe").with(user("admin").roles("ADMIN")).with(csrf()))
            .andExpect(status().isOk)
        mvc.perform(post("/admin/review-probe").with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden)
    }

    @Test fun `email token minting is denied for anonymous and admin`() {
        mvc.perform(get("/admin/auth/access-token").param("email", "test@example.invalid"))
            .andExpect(status().isUnauthorized)
        mvc.perform(get("/admin/auth/access-token").with(user("admin").roles("ADMIN")))
            .andExpect(status().isForbidden)
    }

    @Test fun `configured management account can login`() {
        mvc.perform(post("/login").with(csrf()).param("username", "test-admin").param("password", "test-only-password"))
            .andExpect(status().is3xxRedirection).andExpect(redirectedUrl("/"))
    }

    @Test fun `fallback blocks unmatched endpoints`() {
        mvc.perform(get("/unmatched").with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden)
    }

    @Configuration
    @EnableWebMvc
    @Import(SecurityConfig::class, AdminAccountConfig::class, GlobalSecurityConfig::class, SecurityResponseHandler::class, ProbeController::class)
    class Config {
        @Bean fun objectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()
        @Bean fun authenticationTokenService() = AuthenticationTokenService(
            JwtTokenProvider(Base64.getEncoder().encodeToString(ByteArray(64)), 60_000, 3_600_000))
    }

    @RestController
    class ProbeController {
        @RequestMapping("/admin/review-probe", method = [RequestMethod.GET, RequestMethod.POST])
        fun probe() = "ok"
    }
}
