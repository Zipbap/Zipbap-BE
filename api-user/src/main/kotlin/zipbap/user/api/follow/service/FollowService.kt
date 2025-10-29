package zipbap.user.api.follow.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.follow.converter.FollowConverter
import zipbap.user.api.follow.dto.FollowResponseDto
import zipbap.global.domain.follow.Follow
import zipbap.global.domain.follow.FollowRepository
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class FollowService(
        private val followRepository: FollowRepository,
        private val userRepository: UserRepository
) {
    @Transactional
    fun follow(follower: User, followingId: Long): FollowResponseDto.FollowCountDto {
        val followingUser = userRepository.findById(followingId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }
        if (followRepository.existsByFollowingAndFollower(followingUser, follower)) {
            throw GeneralException(ErrorStatus.ALREADY_FOLLOW_EXIST)
        }

        val follow = Follow(followingUser, follower)
        followRepository.save(follow)

        val followerCount = followRepository.countByFollowing(followingUser)
        val followingCount = followRepository.countByFollower(followingUser)

        return FollowConverter.toCountDto(followingId, followingCount, followerCount, true)
    }

    @Transactional
    fun unfollow(unfollower: User, unfollowingId: Long): FollowResponseDto.FollowCountDto {
        val unfollowingUser = userRepository.findById(unfollowingId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }
        if (!followRepository.existsByFollowingAndFollower(unfollowingUser, unfollower)) {
            throw GeneralException(ErrorStatus.FOLLOW_NOT_FOUND)
        }


        followRepository.deleteByFollowingAndFollower(unfollowingUser, unfollower)

        val followingCount = followRepository.countByFollowing(unfollowingUser)
        val followerCount = followRepository.countByFollower(unfollowingUser)

        return FollowConverter.toCountDto(unfollowingId, followingCount, followerCount, false)
    }

    /**
     * user가 userId의 팔로워인지 확인하고, userId의 팔로워 및 팔로잉 수를 조회합니다.
     */
    fun count(user: User, userId: Long): FollowResponseDto.FollowCountDto {
        val foundUser = userRepository.findById(userId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }
        val isFollowing = followRepository.existsByFollowingAndFollower(foundUser, user)

        val followerCount = followRepository.countByFollowing(foundUser)
        val followingCount = followRepository.countByFollower(foundUser)
        return FollowConverter.toCountDto(userId, followingCount, followerCount, isFollowing)
    }

    /**
     * 특정 대상이 팔로잉하는 이용자들의 리스트를 반환합니다.
     * 현재 조회하는 유저를 기준으로 팔로잉 여부도 같이 반환합니다.
     * 특정 유저가 팔로잉 한다 -> following.follower에 저장되어있다
     */
    fun followingList(user: User, followingCheckUserId: Long,
                      condition: String?): List<FollowResponseDto.FollowUserDto> {
        val followingTargetUser = userRepository.findById(followingCheckUserId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val targetFollowing = followRepository.searchFollowingList(followingCheckUserId, condition)
        val userFollowing = followRepository.findByFollower(user)

        val userSet = userFollowing.mapNotNull {
            it.following.id
        }.toHashSet()

        val targetUserList = targetFollowing.map {fol ->
            val u = fol.following
            FollowConverter.toUserDto(u, u.id in userSet)
        }.toList()

        return targetUserList
    }

    /**
     * 특정 대상을 팔로우하는 이용자들(팔로워)의 리스트를 반환합니다.
     * 현재 조회하는 유저를 기준으로 팔로잉 여부도 같이 반환합니다.
     * 특정 유저가 팔로워의 팔로워를 조회하고싶다 -> following.following에 저장되어있다
     */
    fun followerList(user: User, followerCheckUserId: Long,
                     condition: String?): List<FollowResponseDto.FollowUserDto> {
        val followerTargetUser = userRepository.findById(followerCheckUserId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val targetFollower = followRepository.searchFollowerList(followerCheckUserId, condition)
        val userFollowing = followRepository.findByFollower(user)

        val userSet = userFollowing.mapNotNull {
            it.following.id
        }.toHashSet()

        val targetUserList = targetFollower.map { fol ->
            val u = fol.follower
            FollowConverter.toUserDto(u, u.id in userSet)
        }.toList()

        return targetUserList
    }

}