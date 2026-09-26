package zipbap.global.global.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@ConfigurationProperties("spring.security.user")
data class AdminAccountProperties(var name: String = "", var password: String = "")

@Configuration
@EnableConfigurationProperties(AdminAccountProperties::class)
class AdminAccountConfig {
    @Bean
    fun managementUserDetailsService(properties: AdminAccountProperties): UserDetailsService {
        // Missing credentials disable management login instead of creating a default account.
        if (properties.name.isBlank() || properties.password.isBlank()) return InMemoryUserDetailsManager()
        require(properties.password != "change-me" && properties.password != "{noop}change-me") {
            "Configure a non-default management password"
        }
        return InMemoryUserDetailsManager(User.withUsername(properties.name)
            .password(properties.password).roles("ADMIN").build())
    }
}
