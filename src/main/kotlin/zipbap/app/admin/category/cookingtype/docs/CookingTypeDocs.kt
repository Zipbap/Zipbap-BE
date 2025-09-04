package zipbap.app.admin.category.cookingtype.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import zipbap.app.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.app.admin.category.cookingtype.dto.CookingTypeResponseDto
import zipbap.app.global.ApiResponse

@RequestMapping("/admin/cooking-types")
@Tag(name = "Cooking Type", description = "요리 종류 관리 API")
interface CookingTypeDocs {

    @Operation(summary = "요리 종류 등록", description = "새로운 요리 종류를 등록합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "등록 성공",
            content = [Content(schema = Schema(implementation = CookingTypeResponseDto::class))]
        )
    )
    @PostMapping
    fun createCookingType(
        @Parameter(description = "요리 종류 생성 요청 DTO")
        @Valid @RequestBody
        dto: CookingTypeRequestDto.CreateCookingTypeDto
    ): ApiResponse<CookingTypeResponseDto>


    @Operation(summary = "요리 종류 수정", description = "기존 요리 종류를 수정합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = [Content(schema = Schema(implementation = CookingTypeResponseDto::class))]
        )
    )
    @PutMapping("/{id}")
    fun updateCookingType(
        @Parameter(description = "요리 종류 ID") @PathVariable id: Long,
        @Parameter(description = "요리 종류 수정 요청 DTO")
        @Valid @RequestBody
        dto: CookingTypeRequestDto.UpdateCookingTypeDto
    ): ApiResponse<CookingTypeResponseDto>


    @Operation(summary = "요리 종류 단건 조회", description = "ID로 요리 종류를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = CookingTypeResponseDto::class))]
        )
    )
    @GetMapping("/{id}")
    fun getCookingType(
        @Parameter(description = "요리 종류 ID") @PathVariable id: Long
    ): ApiResponse<CookingTypeResponseDto>


    @Operation(summary = "요리 종류 전체 조회", description = "등록된 모든 요리 종류를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [Content(schema = Schema(implementation = CookingTypeResponseDto::class))]
        )
    )
    @GetMapping
    fun getAllCookingTypes(): ApiResponse<List<CookingTypeResponseDto>>


    @Operation(summary = "요리 종류 삭제", description = "ID로 요리 종류를 삭제합니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "삭제 성공")
    )
    @DeleteMapping("/{id}")
    fun deleteCookingType(
        @Parameter(description = "요리 종류 ID") @PathVariable id: Long
    ): ApiResponse<Unit>
}
