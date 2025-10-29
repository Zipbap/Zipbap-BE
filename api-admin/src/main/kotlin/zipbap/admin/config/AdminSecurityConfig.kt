package zipbap.admin.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource
import zipbap.global.global.auth.filter.CustomJwtFilter
import zipbap.global.global.auth.filter.JwtExceptionFilter
import zipbap.global.global.auth.service.TokenService

@EnableWebSecurity
@Configuration
class SecurityConfig(
        private val tokenService: TokenService,
        private val objectMapper: ObjectMapper,
        private val corsConfigurationSource: CorsConfigurationSource
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
//                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
//                            .requestMatchers(("/admin/**")).hasRole("ADMIN")
                            .anyRequest().permitAll()
                }
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .cors { it.configurationSource(corsConfigurationSource) }

        return http.build()
    }

    // ---- Chain 2: API/JWT/OAuth2 - Stateless, CSRF Off ----
    @Bean
    @Order(3)
    fun apiChain(http: HttpSecurity): SecurityFilterChain {
        http
                .securityMatcher("/api/**", "/login/**")
                .cors { it.configurationSource(corsConfigurationSource) }
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

        // 필터 순서: ExceptionFilter -> JwtFilter -> UsernamePasswordAuthenticationFilter
        val jwtFilter = CustomJwtFilter(tokenService)
        val exFilter = JwtExceptionFilter(objectMapper)

        http.addFilterBefore(exFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}

