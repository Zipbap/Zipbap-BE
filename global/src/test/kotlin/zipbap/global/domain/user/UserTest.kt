package zipbap.global.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import support.fixture.UserFixture

class UserTest {

    @Test
    @DisplayName("유저 update를 통해 내부 필드를 변경한다.")
    fun update() {
        //given
        val user = UserFixture.create()

        //when
        user.update(
            nickname = "집밥",
            isPrivate = true,
            profileImage = "www.naver.com",
            statusMessage = "취업하고 싶다."
        );

        //then
        assertAll(
            { assertThat(user.nickname).isEqualTo("집밥") },
            { assertThat(user.isPrivate).isTrue() },
            { assertThat(user.profileImage).isEqualTo("www.naver.com") },
            { assertThat(user.statusMessage).isEqualTo("취업하고 싶다.") }
        )
    }

}