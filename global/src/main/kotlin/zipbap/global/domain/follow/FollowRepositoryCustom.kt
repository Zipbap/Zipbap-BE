package zipbap.global.domain.follow

import zipbap.global.domain.follow.Follow

interface FollowRepositoryCustom {

    fun searchFollowingList(userId: Long, searchCondition: String?): List<Follow>

    fun searchFollowerList(userId: Long, searchCondition: String?): List<Follow>
}