package zipbap.user.api.mycategory.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.mycategory.dto.MyCategoryRequestDto
import zipbap.user.api.mycategory.dto.MyCategoryResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.Parameter

@Tag(name = "My Categories", description = "내 카테고리 관리 API (개인 사용자 전용)")
@RequestMapping("/api/my-categories")
interface MyCategoryDocs {

    @Operation(
        summary = "내 카테고리 생성",
        description = """
        사용자가 직접 **개인화 카테고리**를 생성합니다.
        - 동일한 이름의 카테고리는 **중복 생성 불가**
        - 생성된 카테고리는 사용자 프로필에 귀속됩니다.
        """,
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "생성 성공",
                content = [Content(schema = Schema(implementation = MyCategoryResponseDto::class))]
            )
        ]
    )
    @PostMapping
    fun createMyCategory(
        @RequestBody dto: MyCategoryRequestDto.CreateMyCategoryDto,
        @UserInjection user: User
    ): ApiResponse<MyCategoryResponseDto>


    @Operation(
        summary = "내 카테고리 수정",
        description = """
        사용자가 본인의 내 카테고리 명칭을 수정합니다.
        - 본인이 아닌 경우 **FORBIDDEN**
        """,
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = [Content(schema = Schema(implementation = MyCategoryResponseDto::class))]
            )
        ]
    )
    @PutMapping("/{id}")
    fun updateMyCategory(
        @Parameter(description = "수정할 카테고리 ID")
        @PathVariable("id") id: String,
        @RequestBody dto: MyCategoryRequestDto.UpdateMyCategoryDto,
        @UserInjection user: User
    ): ApiResponse<MyCategoryResponseDto>


    @Operation(
        summary = "내 카테고리 목록 조회",
        description = """
        사용자가 보유한 **모든 내 카테고리 목록**을 조회합니다.
        """
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = MyCategoryResponseDto::class))]
            )
        ]
    )
    @GetMapping
    fun getMyCategories(
        @UserInjection user: User
    ): ApiResponse<List<MyCategoryResponseDto>>


    @Operation(
        summary = "내 카테고리 삭제",
        description = """
        사용자가 본인의 카테고리를 삭제합니다.
        
        ### 주의
        - 해당 카테고리를 사용 중이던 레시피는 `myCategory = null` 로 자동 해제됩니다.
        - 본인이 아닌 경우 **FORBIDDEN**
        """,
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "삭제 성공"
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteMyCategory(
        @Parameter(description = "삭제할 카테고리 ID")
        @PathVariable("id") id: String,
        @UserInjection user: User
    ): ApiResponse<Unit>
}
