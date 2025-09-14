package zipbap.app.api.mypage.dto

import org.springframework.data.domain.Page

data class MyPageResponseDto(
        val profileBlock: ProfileBlock,
        val recipeCardDtoPage: Page<RecipeCardDto>,
        val isOwner: Boolean,
        val isFeed: Boolean
)