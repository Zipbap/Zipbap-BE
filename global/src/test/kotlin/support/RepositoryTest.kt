package support

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import zipbap.global.global.config.QueryDslConfig


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@Import(TestAuditingConfiguration::class, QueryDslConfig::class)
@ActiveProfiles("test")
annotation class RepositoryTest()
