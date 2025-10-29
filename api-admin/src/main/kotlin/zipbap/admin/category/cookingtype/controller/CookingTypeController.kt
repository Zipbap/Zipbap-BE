package zipbap.admin.category.cookingtype.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.admin.category.cookingtype.docs.CookingTypeDocs
import zipbap.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.admin.category.cookingtype.dto.CookingTypeResponseDto
import zipbap.admin.category.cookingtype.service.CookingTypeService
import zipbap.app.global.ApiResponse

@RestController
class CookingTypeController(
    private val cookingTypeService: CookingTypeService
) : CookingTypeDocs {

    override fun createCookingType(dto: CookingTypeRequestDto.CreateCookingTypeDto): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.onSuccess(cookingTypeService.createCookingType(dto))

    override fun updateCookingType(id: Long, dto: CookingTypeRequestDto.UpdateCookingTypeDto): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.onSuccess(cookingTypeService.updateCookingType(id, dto))

    override fun getCookingType(id: Long): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.onSuccess(cookingTypeService.getCookingType(id))

    override fun getAllCookingTypes(): ApiResponse<List<CookingTypeResponseDto>> =
        ApiResponse.onSuccess(cookingTypeService.getAllCookingTypes())

    override fun deleteCookingType(id: Long): ApiResponse<Unit> =
        ApiResponse.onSuccess(cookingTypeService.deleteCookingType(id))
}
