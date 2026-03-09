package support.fixture

import zipbap.global.domain.user.SocialType
import zipbap.global.domain.user.User
import java.util.UUID

object UserFixture {

    fun create(
        // 💡 email은 Unique 제약조건이 있으므로, 여러 유저를 저장할 때
        // 충돌(DataIntegrityViolationException)이 나지 않도록 랜덤값을 섞어줍니다.
        email: String = "test-${UUID.randomUUID().toString().substring(0, 6)}@zipbap.com",

        nickname: String = "집밥선생",
        socialType: SocialType = SocialType.KAKAO, // 지난번 테스트 코드에서 쓰신 KAKAO를 기본값으로!
        isPrivate: Boolean = false,
        profileImage: String? = "https://cdn.zipbap.com/default_profile.png",
        statusMessage: String? = "안녕하세요! 요리를 사랑하는 집밥러입니다.",

        // 💡 JPA가 ID를 자동 생성(GenerationType.IDENTITY)하게 두려면 null이 가장 좋습니다.
        id: Long? = null
    ): User {
        return User(
            email = email,
            nickname = nickname,
            socialType = socialType,
            isPrivate = isPrivate,
            profileImage = profileImage,
            statusMessage = statusMessage,
            id = id
        )
    }
}