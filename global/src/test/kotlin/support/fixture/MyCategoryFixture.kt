package support.fixture

import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.user.User
import java.util.UUID

object MyCategoryFixture {

    fun create(
        // 💡 필수: 영속화(save)된 User 객체를 받아와야 합니다.
        user: User,

        // 💡 Unique 제약조건(user_id + name) 충돌을 피하기 위해 랜덤값을 기본으로 덧붙여줍니다.
        // 테스트에서 특정 이름이 꼭 필요하다면 호출할 때 name = "한식" 처럼 덮어쓰면 됩니다.
        name: String = "나만의 카테고리-${UUID.randomUUID().toString().substring(0, 4)}",

        // MC-{userId}-{sequence} 규격을 흉내 낸 가짜 ID
        id: String = "MC-TEST-${UUID.randomUUID().toString().substring(0, 4)}"
    ): MyCategory {
        return MyCategory(
            user = user,
            name = name,
            id = id
        )
    }
}