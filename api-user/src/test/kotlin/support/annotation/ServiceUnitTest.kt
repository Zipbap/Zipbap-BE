package support.annotation

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(MockKExtension::class) // 💡 코틀린 전용 Mock 라이브러리인 MockK를 활성화합니다.
annotation class ServiceUnitTest
