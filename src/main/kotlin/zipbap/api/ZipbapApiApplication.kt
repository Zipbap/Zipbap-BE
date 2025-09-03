package zipbap.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class ZipbapApiApplication

fun main(args: Array<String>) {
	runApplication<ZipbapApiApplication>(*args)
}
