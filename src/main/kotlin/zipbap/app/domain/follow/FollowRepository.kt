package zipbap.app.domain.follow

import org.springframework.data.jpa.repository.JpaRepository
import zipbap.app.domain.user.User

interface FollowRepository : JpaRepository<Follow, Long>, FollowRepositoryCustom {

    fun existsByFollowingAndFollower(following: User, follower: User): Boolean

    fun countByFollowing(following: User): Long
    fun countByFollower(follower: User): Long

    fun deleteByFollowingAndFollower(following: User, follower: User)

    fun findByFollowing(following: User): List<Follow>
    fun findByFollower(follower: User): List<Follow>
}