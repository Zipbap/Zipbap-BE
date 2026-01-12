package zipbap.user.api.category.controller

import zipbap.global.domain.user.User
import org.springframework.web.bind.annotation.RestController
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.category.docs.CategoryDocs
import zipbap.user.api.category.dto.CategoryResponseDto
import zipbap.user.api.category.service.CategoryService
import zipbap.app.global.ApiResponse

@RestController
class CategoryController(
    private val categoryService: CategoryService
) : CategoryDocs {

    override fun getAllCategories(@UserInjection userId: Long):
            ApiResponse<CategoryResponseDto.CategoryListResponseDto> =
        ApiResponse.onSuccess(categoryService.getAllCategories(userId))
}
