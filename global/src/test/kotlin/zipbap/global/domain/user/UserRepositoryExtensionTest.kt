package zipbap.global.domain.user

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import zipbap.global.global.exception.GeneralException

class UserRepositoryExtensionTest {

    // 💡 가짜(Mock) UserRepository를 만듭니다. 스프링 띄울 필요 없음!
    private val userRepository: UserRepository = mockk()

    @Test
    @DisplayName("유저가 존재하지 않으면 GeneralException(USER_NOT_FOUND)을 던진다.")
    fun getByIdThrowsExceptionWhenUserNotFound() {
        // given: findByIdOrNull이 호출되면 무조건 null을 반환하도록 조작
        every { userRepository.findByIdOrNull(any()) } returns null

        // when & then: 확장 함수를 호출했을 때 우리가 의도한 예외가 터지는지 검증
        val exception = assertThrows<GeneralException> {
            userRepository.findByIdOrThrow(1L)
        }

    }
}