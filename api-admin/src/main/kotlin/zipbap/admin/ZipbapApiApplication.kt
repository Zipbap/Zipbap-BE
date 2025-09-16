package zipbap.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
		scanBasePackages = [
			"zipbap.admin",
			"zipbap.global"
		]
)
@EntityScan("zipbap.global")
@EnableJpaRepositories("zipbap.global")
@EnableJpaAuditing
class ZipbapApiApplication

fun main(args: Array<String>) {
	runApplication<ZipbapApiApplication>(*args)
}
