package zipbap.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ZipbapApiApplication

fun main(args: Array<String>) {
	runApplication<ZipbapApiApplication>(*args)
}
