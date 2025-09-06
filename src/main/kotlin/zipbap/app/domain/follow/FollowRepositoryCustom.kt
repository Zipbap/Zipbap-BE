package zipbap.app.domain.follow

interface FollowRepositoryCustom {

    fun searchFollowingList(userId: Long, searchCondition: String?): List<Follow>

    fun searchFollowerList(userId: Long, searchCondition: String?): List<Follow>
}