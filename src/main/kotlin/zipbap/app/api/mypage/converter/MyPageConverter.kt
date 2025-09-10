package zipbap.app.api.mypage.converter

import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.api.follow.dto.FollowResponseDto
import zipbap.app.api.mypage.dto.MyPageResponseDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.user.dto.UserResponseDto

object MyPageConverter {

    fun toBookmarkDto(
            bookmarkRecipeResponseDtos: List<BookmarkResponseDto.BookmarkRecipeResponseDto>,
            followCountDto: FollowResponseDto.FollowCountDto,
            profileDto: UserResponseDto.UserProfileDto
    ): MyPageResponseDto.BookmarkDto {
        return MyPageResponseDto.BookmarkDto(
                bookmarkRecipesDto = bookmarkRecipeResponseDtos,
                followCountDto = followCountDto,
                profileDto = profileDto
        )
    }


    fun toFeedDto(feedList: List<RecipeResponseDto.FeedResponseDto>,
            followCountDto: FollowResponseDto.FollowCountDto,
                  profileDto: UserResponseDto.UserProfileDto,
                  isOwner: Boolean
    ): MyPageResponseDto.FeedDto {
        return MyPageResponseDto.FeedDto(
                followCountDto = followCountDto,
                profileDto = profileDto,
                feedRecipesDto = feedList,
                isOwner = isOwner
        )
    }
}