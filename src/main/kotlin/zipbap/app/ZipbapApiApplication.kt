package zipbap.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["zipbap.app.domain", "zipbap.api.domain", "zipbap.app.api"])
@EnableJpaRepositories(basePackages = ["zipbap.app.domain", "zipbap.app.api"])
@EnableJpaAuditing
class ZipbapApiApplication

fun main(args: Array<String>) {
	runApplication<ZipbapApiApplication>(*args)
}
