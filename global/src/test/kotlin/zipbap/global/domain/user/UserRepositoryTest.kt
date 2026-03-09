package zipbap.global.domain.user

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.annotation.RepositoryTest

@RepositoryTest
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
) {

    @Test
    @DisplayName("이메일 존재여부를 조회한다. 존재하면 true를 반환한다.")
    fun existsByEmail() {
        //given
        val email = "changjun157@naver.com"
        val user = User(
            email = email,
            nickname = "changjun157",
            socialType = SocialType.KAKAO,
            isPrivate = false,
        )

        userRepository.save(user);

        //when
        val result = userRepository.existsByEmail(email)

        //then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("이메일 존재여부를 조회한다. 존재하지 않으면 false를 반환한다.")
    fun noExistsByEmail() {
        //given
        val email = "changjun157@naver.com"

        //when
        val result = userRepository.existsByEmail(email)

        //then
        assertThat(result).isFalse()

    }

    @Test
    @DisplayName("이메일을 통해 저장된 유저를 조회한다.")
    fun findByEmail() {
        //given
        val email = "changjun157"
        val user = User(
            email = email,
            nickname = email,
            socialType = SocialType.KAKAO,
            isPrivate = false,
        )

        userRepository.save(user)

        //when
        val foundUser = userRepository.findByEmail(email)

        //then
        assertThat(foundUser).isPresent()
        assertThat(foundUser.get().email).isEqualTo(email)
    }

    @Test
    @DisplayName("이메일로 유저를 조회한다. 해당하는 이메일이 없을 경우, 빈 값을 반환한다.")
    fun findByNoEmail() {
        //given
        val email = "changjun157"

        //when
        val foundUser = userRepository.findByEmail(email)

        //then
        assertThat(foundUser).isEmpty()
    }

}