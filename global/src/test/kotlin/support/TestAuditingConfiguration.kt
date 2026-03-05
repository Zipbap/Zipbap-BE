package support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional


@EnableJpaAuditing
@TestConfiguration
class TestAuditingConfiguration {
    @Bean
    fun auditorAware(): AuditorAware<String?> {
        return AuditorAware { Optional.of("test-user") }
    }
}