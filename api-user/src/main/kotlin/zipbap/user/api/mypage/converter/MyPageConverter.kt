package zipbap.user.api.mypage.converter

import org.springframework.data.domain.Page
import zipbap.global.domain.mypage.MyPageQueryResult
import zipbap.user.api.mypage.dto.MyPageResponseDto

object MyPageConverter {

    fun toDto(
            profileBlock: MyPageQueryResult.ProfileBlock,
            recipeCard: Page<MyPageQueryResult.RecipeCard>,
            isOwner: Boolean,
            isFeed: Boolean): MyPageResponseDto.MyPageViewDto {
        return MyPageResponseDto.MyPageViewDto(
                profileBlockDto = MyPageResponseDto
                        .ProfileDto(profileBlock),
                recipeCardDtoPage = recipeCard
                        .map { MyPageResponseDto.RecipeCardDto(it)},
                isOwner = isOwner,
                isFeed = isFeed
        )
    }

}



