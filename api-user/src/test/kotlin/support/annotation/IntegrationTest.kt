package support.annotation

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest // 💡 스프링의 모든 빈(Bean)을 로드하여 실제 환경과 똑같이 구성합니다.
@Transactional  // 💡 테스트가 끝나면 DB에 저장된 데이터를 싹 롤백(Rollback)해 줍니다.
@ActiveProfiles("test") // 💡 application-test.yml 설정을 사용하도록 강제합니다.
annotation class IntegrationTest