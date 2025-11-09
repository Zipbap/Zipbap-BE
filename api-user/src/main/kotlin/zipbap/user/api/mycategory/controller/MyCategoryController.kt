package zipbap.user.api.mycategory.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.user.api.mycategory.dto.MyCategoryRequestDto
import zipbap.user.api.mycategory.dto.MyCategoryResponseDto
import zipbap.user.api.mycategory.service.MyCategoryService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.user.api.mycategory.docs.MyCategoryDocs

@RestController
@RequestMapping("/api/my-categories")
@Tag(name = "My Categories", description = "내 카테고리 관리 API (개인 사용자 전용)")
class MyCategoryController(
    private val myCategoryService: MyCategoryService
) : MyCategoryDocs {

    override fun createMyCategory(
        dto: MyCategoryRequestDto.CreateMyCategoryDto,
        user: User
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.createMyCategory(dto, user.id!!))

    override fun updateMyCategory(
        id: String,
        dto: MyCategoryRequestDto.UpdateMyCategoryDto,
        user: User
    ): ApiResponse<MyCategoryResponseDto> =
        ApiResponse.onSuccess(myCategoryService.updateMyCategory(id, dto, user.id!!))

    override fun getMyCategories(
        user: User
    ): ApiResponse<List<MyCategoryResponseDto>> =
        ApiResponse.onSuccess(myCategoryService.getMyCategories(user.id!!))

    override fun deleteMyCategory(
        id: String,
        user: User
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(myCategoryService.deleteMyCategory(id, user.id!!))
}

