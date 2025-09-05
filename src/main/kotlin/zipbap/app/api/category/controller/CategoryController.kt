package zipbap.app.api.category.controller

import zipbap.app.domain.user.User
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.resolver.UserInjection
import zipbap.app.api.category.docs.CategoryDocs
import zipbap.app.api.category.dto.CategoryResponseDto
import zipbap.app.api.category.service.CategoryService
import zipbap.app.global.ApiResponse

@RestController
class CategoryController(
    private val categoryService: CategoryService
) : CategoryDocs {

    override fun getAllCategories(@UserInjection user: User ):
            ApiResponse<CategoryResponseDto.CategoryListResponseDto> =
        ApiResponse.onSuccess(categoryService.getAllCategories(user.id!!))
}
