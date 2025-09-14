package zipbap.app.api.mypage.converter

import org.springframework.data.domain.Page
import zipbap.app.api.mypage.dto.MyPageResponseDto
import zipbap.app.api.mypage.dto.ProfileBlock
import zipbap.app.api.mypage.dto.RecipeCardDto

object MyPageConverter {

    fun toDto(
            profileBlock: ProfileBlock,
            recipeCardDto: Page<RecipeCardDto>,
            isOwner: Boolean,
            isFeed: Boolean): MyPageResponseDto {
        return MyPageResponseDto(
                profileBlock = profileBlock,
                recipeCardDtoPage = recipeCardDto,
                isOwner = isOwner,
                isFeed = isFeed
        )
    }

}



