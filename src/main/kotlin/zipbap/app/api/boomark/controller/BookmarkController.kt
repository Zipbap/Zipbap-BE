package zipbap.app.api.boomark.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.boomark.docs.BookmarkDocs
import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.api.boomark.service.BookmarkService
import zipbap.app.api.like.dto.LikeResponseDto
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/bookmarks")
class BookmarkController(
        private val bookmarkService: BookmarkService
) : BookmarkDocs{

    override fun markRecipe(user: User, recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto> =
            ApiResponse.onSuccess(bookmarkService.markRecipe(user, recipeId))

    override fun unmarkRecipe(user: User, recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto> =
            ApiResponse.onSuccess(bookmarkService.unmarkRecipe(user, recipeId))

    override fun countBookmarks(recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto> =
            ApiResponse.onSuccess(bookmarkService.countBookmarks(recipeId))

    override fun userRecipes(user: User
    ): ApiResponse<List<BookmarkResponseDto.BookmarkRecipeResponseDto>> =
            ApiResponse.onSuccess(bookmarkService.getMarkedRecipe(user))
}