package zipbap.app.api.mypage.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.api.boomark.service.BookmarkService
import zipbap.app.api.follow.service.FollowService
import zipbap.app.api.mypage.converter.MyPageConverter
import zipbap.app.api.mypage.dto.MyPageResponseDto
import zipbap.app.api.recipe.service.RecipeService
import zipbap.app.api.user.converter.UserConverter
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

@Service
@Transactional(readOnly = true)
class MyPageService(
        private val userRepository: UserRepository,
        private val bookmarkService: BookmarkService,
        private val followService: FollowService,
        private val recipeService: RecipeService
) {

    fun getBookmarkVersion(viewer: User,
                           ownerId: Long) : MyPageResponseDto.BookmarkDto {
        if (ownerId != viewer.id) {
            throw GeneralException(ErrorStatus.UNAUTHORIZED)
        }
        val bookmarkRecipeResponseDtos = bookmarkService.getMarkedRecipe(viewer)
        val followCountDto = followService.count(viewer, ownerId)
        val profileDto = UserConverter.toProfileDto(viewer)

       return MyPageConverter.toBookmarkDto(bookmarkRecipeResponseDtos,
                followCountDto,
                profileDto)
    }

    fun getFeedVersion(viewer: User, ownerId: Long): MyPageResponseDto.FeedDto {
        val owner = userRepository.findById(ownerId).orElseThrow {
            GeneralException(ErrorStatus.USER_NOT_FOUND)
        }

        val feedList = recipeService.getFeedList(ownerId)
        val followCountDto = followService.count(viewer, ownerId)
        val profileDto = UserConverter.toProfileDto(viewer)
        val isOwner = viewer.id == ownerId

        return MyPageConverter.toFeedDto(feedList,
                followCountDto,
                profileDto,
                isOwner
        )
    }

}