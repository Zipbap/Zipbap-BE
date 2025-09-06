package zipbap.app.api.boomark.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.boomark.dto.BookmarkResponseDto
import zipbap.app.api.boomark.service.BookmarkService
import zipbap.app.api.like.dto.LikeResponseDto
import zipbap.app.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/bookmarks")
class BookmarkController(
        private val bookmarkService: BookmarkService
) {

    @PostMapping("/{recipeId}")
    fun markRecipe(@UserInjection user: User, @PathVariable recipeId: String): ApiResponse<BookmarkResponseDto> =
            ApiResponse.onSuccess(bookmarkService.markRecipe(user, recipeId))

    @DeleteMapping("/{recipeId}")
    fun unmarkRecipe(@UserInjection user: User, @PathVariable recipeId: String): ApiResponse<BookmarkResponseDto> =
            ApiResponse.onSuccess(bookmarkService.unmarkRecipe(user, recipeId))

    @GetMapping("/{recipeId}/count")
    fun countBookmarks(@PathVariable recipeId: String): ApiResponse<BookmarkResponseDto> =
            ApiResponse.onSuccess(bookmarkService.countBookmarks(recipeId))
}