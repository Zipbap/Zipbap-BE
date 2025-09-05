package zipbap.app.api.category.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.category.docs.CategoryDocs
import zipbap.app.api.category.dto.CategoryResponseDto
import zipbap.app.api.category.service.CategoryService
import zipbap.app.global.ApiResponse

@RestController
class CategoryController(
    private val categoryService: CategoryService
) : CategoryDocs {

    override fun getAllCategories(userId: Long): ApiResponse<CategoryResponseDto.CategoryListResponseDto> =
        ApiResponse.onSuccess(categoryService.getAllCategories(userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정
}
