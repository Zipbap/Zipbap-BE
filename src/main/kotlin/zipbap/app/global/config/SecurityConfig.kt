package zipbap.app.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import zipbap.app.api.auth.domain.token.service.TokenService
import zipbap.app.api.auth.filter.CustomJwtFilter
import zipbap.app.api.auth.filter.JwtExceptionFilter
import zipbap.app.api.auth.handler.CustomFailureHandler
import zipbap.app.api.auth.handler.CustomSuccessHandler
import zipbap.app.api.auth.service.CustomOAuth2UserService

@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(SwaggerAdminProps::class)
class SecurityConfig(
        private val customOAuth2UserService: CustomOAuth2UserService,
        private val customSuccessHandler: CustomSuccessHandler,
        private val customFailureHandler: CustomFailureHandler,
        private val tokenService: TokenService,
        private val objectMapper: ObjectMapper,
        private val swaggerAdminProps: SwaggerAdminProps,
) {

    @Bean
    @Order(1)
    fun webResourceChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/static/js/**", "/static/images/**", "/static/css/**", "/static/scss/**")
                .authorizeHttpRequests {auth ->
                    auth
                            .anyRequest().permitAll()
                }

        return http.build()
    }
    // ---- Chain 1: Swagger & (선택) Admin - FormLogin, 세션 사용, CSRF 유지 ----
    @Bean
    @Order(2)
    fun swaggerChain(http: HttpSecurity): SecurityFilterChain {
        http
                .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/admin/**",
                        "/login", "/logout")
                .authorizeHttpRequests { auth ->
                    auth
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                            .requestMatchers(("/admin/**")).hasRole("ADMIN")
                            .anyRequest().permitAll()
                }
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .cors { it.configurationSource(corsConfigurationSource()) }
                .userDetailsService(userDetailsService())

        return http.build()
    }

    // ---- Chain 2: API/JWT/OAuth2 - Stateless, CSRF Off ----
    @Bean
    @Order(3)
    fun apiChain(http: HttpSecurity): SecurityFilterChain {
        http
                .securityMatcher("/api/**", "/oauth2/**", "/oauth2Login/**", "/login/**")
                .cors { it.configurationSource(corsConfigurationSource()) }
                .csrf { it.disable() }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authorizeHttpRequests { authorize ->
                    authorize
                            .requestMatchers(
                                    "/", "/error",
                                    "/oauth2/**", "/oauth2Login/**", "/login/**",
                                    "/api/v1/auth/**", "/api/auth/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                }
                .oauth2Login { oauth2 ->
                    oauth2
                            .userInfoEndpoint { it.userService(customOAuth2UserService) }
                            .successHandler(customSuccessHandler)
                            .failureHandler(customFailureHandler)
                }

        // 필터 순서: ExceptionFilter -> JwtFilter -> UsernamePasswordAuthenticationFilter
        val jwtFilter = CustomJwtFilter(tokenService)
        val exFilter = JwtExceptionFilter(objectMapper)

        http.addFilterBefore(exFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // ---- InMemory Admin (Swagger 접근용) ----

    fun userDetailsService(): UserDetailsService {
        val admin = User.withUsername(swaggerAdminProps.name)
                .password(swaggerAdminProps.password)
                .roles(*swaggerAdminProps.roles)
                .build()
        return InMemoryUserDetailsManager(admin)
    }

    // ---- CORS ----
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf(
                    "https://zipbap.vercel.app",
                    "http://localhost:5173",
                    "https://zipbap.store",
                    "https://www.zipbap.store"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization") // 필요 시
            allowCredentials = true
            maxAge = 3600L
        }
        return UrlBasedCorsConfigurationSource().also {
            it.registerCorsConfiguration("/**", config)
        }
    }
}

@ConfigurationProperties(prefix = "spring.security.user")
data class SwaggerAdminProps(
        var name: String = "admin",
        var password: String = "change-me",
        var roles: Array<String> = arrayOf("ADMIN")
)
