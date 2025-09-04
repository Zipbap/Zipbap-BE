package zipbap.app.api.mycategory.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.app.api.mycategory.dto.MyCategoryRequestDto
import zipbap.app.api.mycategory.dto.MyCategoryResponseDto
import zipbap.app.api.mycategory.service.MyCategoryService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/api/my-categories")
@Tag(name = "My Categories", description = "내 카테고리 관리 API (개인 사용자 전용)")
class MyCategoryController(
    private val myCategoryService: MyCategoryService
) {

    @PostMapping
    fun createMyCategory(
        @RequestBody dto: MyCategoryRequestDto.CreateMyCategoryDto,
        @RequestParam("userId") userId: Long
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.createMyCategory(dto, userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    @PutMapping("/{id}")
    fun updateMyCategory(
        @PathVariable("id") id: String,
        @RequestBody dto: MyCategoryRequestDto.UpdateMyCategoryDto,
        @RequestParam("userId") userId: Long
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.updateMyCategory(id, dto, userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    @GetMapping
    fun getMyCategories(
        @RequestParam("userId") userId: Long
    ): ApiResponse<List<MyCategoryResponseDto>> =
        ApiResponse.onSuccess(myCategoryService.getMyCategories(userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정

    @DeleteMapping("/{id}")
    fun deleteMyCategory(
        @PathVariable("id") id: String,
        @RequestParam("userId") userId: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(myCategoryService.deleteMyCategory(id, userId))
    // TODO: 추후 JWT 적용 시 userId 파라미터 제거 후, SecurityContext 에서 인증 사용자 정보 추출 예정
}
