package zipbap.app.api.mypage.dto

import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.api.follow.dto.FollowResponseDto
import zipbap.app.api.recipe.dto.RecipeResponseDto
import zipbap.app.api.user.dto.UserResponseDto

class MyPageResponseDto {

    data class BookmarkDto(
            val bookmarkRecipesDto: List<BookmarkResponseDto.BookmarkRecipeResponseDto>,
            val profileDto: UserResponseDto.UserProfileDto,
            val followCountDto: FollowResponseDto.FollowCountDto,
            val isOwner: Boolean = true
    )

    data class FeedDto(
            val feedRecipesDto: List<RecipeResponseDto.FeedResponseDto>,
            val profileDto: UserResponseDto.UserProfileDto,
            val followCountDto: FollowResponseDto.FollowCountDto,
            val isOwner: Boolean
    )
}