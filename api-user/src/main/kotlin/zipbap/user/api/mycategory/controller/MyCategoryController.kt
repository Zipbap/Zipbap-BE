package zipbap.user.api.mycategory.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.mycategory.dto.MyCategoryRequestDto
import zipbap.user.api.mycategory.dto.MyCategoryResponseDto
import zipbap.user.api.mycategory.service.MyCategoryService
import zipbap.global.domain.user.User
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
            @UserInjection user: User
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.createMyCategory(dto, user.id!!))

    @PutMapping("/{id}")
    fun updateMyCategory(
            @PathVariable("id") id: String,
            @RequestBody dto: MyCategoryRequestDto.UpdateMyCategoryDto,
            @UserInjection user: User
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.updateMyCategory(id, dto, user.id!!))

    @GetMapping
    fun getMyCategories(
        @UserInjection user: User
    ): ApiResponse<List<MyCategoryResponseDto>> =
        ApiResponse.onSuccess(myCategoryService.getMyCategories(user.id!!))

    @DeleteMapping("/{id}")
    fun deleteMyCategory(
        @PathVariable("id") id: String,
        @UserInjection user: User
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(myCategoryService.deleteMyCategory(id, user.id!!))
}
