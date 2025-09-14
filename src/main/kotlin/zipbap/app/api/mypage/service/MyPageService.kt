package zipbap.app.api.mypage.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.mypage.converter.MyPageConverter
import zipbap.app.api.mypage.dto.MyPageResponseDto
import zipbap.app.domain.mypage.MyPageQueryRepository
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class MyPageService(
        private val userRepository: UserRepository,
        private val myPageQueryRepository: MyPageQueryRepository
) {

    fun getBookmarkCard(
            viewer: User,
            ownerId: Long,
            pageable: Pageable) : MyPageResponseDto {
        if (ownerId != viewer.id) {
            throw GeneralException(ErrorStatus.UNAUTHORIZED) // 저장된 Bookmark는 자기 자신만 볼 수 있다.
        }

        val feedCards = myPageQueryRepository.loadBookmarkCards(ownerId, pageable)
        val profileBlock = myPageQueryRepository.loadProfileBlock(ownerId, viewer.id)

        return MyPageConverter.toDto(profileBlock, feedCards, true, false)
    }

    fun getFeedCard(
            viewer: User,
            ownerId: Long,
            pageable: Pageable): MyPageResponseDto {
        userRepository.findById(ownerId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val feedCards = myPageQueryRepository.loadFeedCards(ownerId, pageable)
        val profileBlock = myPageQueryRepository.loadProfileBlock(ownerId, viewer.id!!)

        val isOwner = (viewer.id == ownerId)

        return MyPageConverter.toDto(profileBlock, feedCards, isOwner, true)
    }

}