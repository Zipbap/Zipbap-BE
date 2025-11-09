package zipbap.user.api.mypage.converter

import org.springframework.data.domain.Page
import zipbap.user.api.mypage.dto.PageResponse
import zipbap.global.domain.mypage.MyPageQueryResult
import zipbap.user.api.mypage.dto.MyPageResponseDto

object MyPageConverter {

    fun toDto(
        profileBlock: MyPageQueryResult.ProfileBlock,
        recipeCard: Page<MyPageQueryResult.RecipeCard>,
        isOwner: Boolean,
        isFeed: Boolean
    ): MyPageResponseDto.MyPageViewDto {

        val pageResponse = PageResponse(
            page = recipeCard.number,
            size = recipeCard.size,
            totalPages = recipeCard.totalPages,
            totalElements = recipeCard.totalElements,
            content = recipeCard.content.map { MyPageResponseDto.RecipeCardDto(it) }
        )

        return MyPageResponseDto.MyPageViewDto(
            profileBlockDto = MyPageResponseDto.ProfileDto(profileBlock),
            recipeCardPage = pageResponse,
            isOwner = isOwner,
            isFeed = isFeed
        )
    }
}
