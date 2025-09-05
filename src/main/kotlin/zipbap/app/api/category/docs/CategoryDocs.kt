package zipbap.app.api.category.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import zipbap.app.api.category.dto.CategoryResponseDto
import zipbap.app.global.ApiResponse

@Tag(name = "Category", description = "카테고리 통합 조회 API")
interface CategoryDocs {

    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리 정보를 조회합니다. (내 카테고리는 userId 기준)")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = CategoryResponseDto.CategoryListResponseDto::class))]
        )
    )
    @GetMapping("/categories")
    fun getAllCategories(
        @Parameter(description = "사용자 ID", example = "1")
        @RequestParam userId: Long
    ): ApiResponse<CategoryResponseDto.CategoryListResponseDto>
}
