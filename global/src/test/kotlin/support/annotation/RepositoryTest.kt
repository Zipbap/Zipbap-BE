package support.annotation

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import support.TestAuditingConfiguration
import zipbap.global.global.config.QueryDslConfig
import zipbap.global.global.config.ClockConfig

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestAuditingConfiguration::class, QueryDslConfig::class, ClockConfig::class)
@ActiveProfiles("test")
annotation class RepositoryTest()
