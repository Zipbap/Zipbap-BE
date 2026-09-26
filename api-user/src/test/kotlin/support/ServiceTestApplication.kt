package support

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import zipbap.global.global.config.QueryDslConfig
import zipbap.user.api.user.service.UserService
import zipbap.user.api.file.service.FileService
import zipbap.user.api.recipe.validator.CategoryValidator

/** Real DB/services without production AWS, OAuth, or management credentials. */
@Configuration
@EnableAutoConfiguration(excludeName = ["io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration"])
@EntityScan("zipbap.global")
@EnableJpaRepositories("zipbap.global")
@Import(TestAuditingConfiguration::class, QueryDslConfig::class, UserService::class, FileService::class, CategoryValidator::class)
class ServiceTestApplication
