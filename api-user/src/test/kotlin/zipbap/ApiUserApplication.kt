package zipbap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["zipbap"])
@EntityScan(basePackages = ["zipbap.global.domain"]) // 💡 엔티티 위치를 명시적으로 알려줌
class ApiUserApplication

fun main(args: Array<String>) {
    runApplication<ApiUserApplication>(*args)
}