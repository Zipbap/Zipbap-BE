package zipbap.user.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource
import zipbap.global.global.auth.filter.CustomJwtFilter
import zipbap.global.global.auth.filter.JwtExceptionFilter
import zipbap.global.global.auth.filter.SecurityResponseHandler
import zipbap.global.global.auth.service.AuthenticationTokenService

@EnableWebSecurity
@Configuration
class UserSecurityConfig(
    private val authenticationTokenService: AuthenticationTokenService,
    private val objectMapper: ObjectMapper,
    private val corsConfigurationSource: CorsConfigurationSource,
    private val errors: SecurityResponseHandler,
    @Qualifier("managementUserDetailsService") private val managementUsers: UserDetailsService
) {
    @Bean
    @Order(1)
    fun webResourceChain(http: HttpSecurity): SecurityFilterChain = http
        .securityMatcher("/static/js/**", "/static/images/**", "/static/css/**", "/static/scss/**")
        .authorizeHttpRequests { it.anyRequest().permitAll() }.build()

    @Bean
    @Order(2)
    fun swaggerChain(http: HttpSecurity): SecurityFilterChain = http
        .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/login", "/logout")
        .authorizeHttpRequests {
            it.requestMatchers("/login", "/logout").permitAll().anyRequest().hasRole("ADMIN")
        }
        .formLogin(Customizer.withDefaults()).logout(Customizer.withDefaults())
        .cors { it.configurationSource(corsConfigurationSource) }
        .userDetailsService(managementUsers).build()

    @Bean
    @Order(3)
    fun apiChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/api/**")
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/api/auth/*/login", "/api/auth/access-token").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/access-token").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { it.authenticationEntryPoint(errors).accessDeniedHandler(errors) }

        http.addFilterBefore(CustomJwtFilter(authenticationTokenService), UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(JwtExceptionFilter(objectMapper), CustomJwtFilter::class.java)
        return http.build()
    }

    @Bean
    @Order(4)
    fun fallbackChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it.anyRequest().denyAll() }
        .exceptionHandling { it.authenticationEntryPoint(errors).accessDeniedHandler(errors) }.build()
}
