package zipbap.user.api.like.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import zipbap.user.api.like.docs.LikeDocs
import zipbap.user.api.like.dto.LikeResponseDto
import zipbap.user.api.like.service.LikeService
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RestController
class LikeController(
    private val likeService: LikeService
) : LikeDocs {

    override fun likeRecipe(@UserInjection user: User, @PathVariable recipeId: String): ApiResponse<LikeResponseDto> =
        ApiResponse.onSuccess(likeService.likeRecipe(user, recipeId))

    override fun unlikeRecipe(@UserInjection user: User, @PathVariable recipeId: String): ApiResponse<LikeResponseDto> =
        ApiResponse.onSuccess(likeService.unlikeRecipe(user, recipeId))

    override fun countLikes(@PathVariable recipeId: String): ApiResponse<LikeResponseDto> =
        ApiResponse.onSuccess(likeService.countLikes(recipeId))
}
