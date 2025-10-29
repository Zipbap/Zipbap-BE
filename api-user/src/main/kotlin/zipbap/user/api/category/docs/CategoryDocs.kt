package zipbap.user.api.category.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.category.dto.CategoryResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@Tag(name = "Category", description = "카테고리 통합 조회 API")
interface CategoryDocs {

    @Operation(
        summary = "모든 카테고리 조회",
        description = "모든 카테고리 정보를 조회합니다. (내 카테고리는 JWT userId 기준)"
    )
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = CategoryResponseDto.CategoryListResponseDto::class))]
        )
    )
    @GetMapping("/api/categories")
    fun getAllCategories(
        @UserInjection user: User
    ): ApiResponse<CategoryResponseDto.CategoryListResponseDto>
}
