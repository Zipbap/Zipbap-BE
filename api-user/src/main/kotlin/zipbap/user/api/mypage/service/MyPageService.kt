package zipbap.user.api.mypage.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.user.api.mypage.converter.MyPageConverter
import zipbap.user.api.mypage.dto.MyPageResponseDto
import zipbap.global.domain.mypage.MyPageQueryRepository
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class MyPageService(
        private val userRepository: UserRepository,
        private val myPageQueryRepository: MyPageQueryRepository
) {

    fun getBookmarkCard(
            viewerId: Long,
            ownerId: Long,
            pageable: Pageable) : MyPageResponseDto.MyPageViewDto {
        if (ownerId != viewerId) {
            throw GeneralException(ErrorStatus.UNAUTHORIZED) // 저장된 Bookmark는 자기 자신만 볼 수 있다.
        }

        val feedCards = myPageQueryRepository.loadBookmarkCards(ownerId, pageable)
        val profileBlock = myPageQueryRepository.loadProfileBlock(ownerId, viewerId)

        return MyPageConverter.toDto(profileBlock, feedCards, true, false)
    }

    fun getFeedCard(
            viewerId: Long,
            ownerId: Long,
            pageable: Pageable): MyPageResponseDto.MyPageViewDto {
        userRepository.findById(ownerId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val feedCards = myPageQueryRepository.loadFeedCards(ownerId, pageable)
        val profileBlock = myPageQueryRepository.loadProfileBlock(ownerId, viewerId)

        val isOwner = (viewerId== ownerId)

        return MyPageConverter.toDto(profileBlock, feedCards, isOwner, true)
    }

}