package zipbap.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["zipbap.app.domain", "zipbap.api.domain"])
@EnableJpaRepositories(basePackages = ["zipbap.app.domain"])
class ZipbapApiApplication

fun main(args: Array<String>) {
	runApplication<ZipbapApiApplication>(*args)
}
