package support.annotation

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import support.ServiceTestApplication

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [ServiceTestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional  // 💡 테스트가 끝나면 DB에 저장된 데이터를 싹 롤백(Rollback)해 줍니다.
@ActiveProfiles("test") // 💡 application-test.yml 설정을 사용하도록 강제합니다.
annotation class IntegrationTest
