package zipbap.app.admin.category.cookingtype.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.app.admin.category.cookingtype.docs.CookingTypeDocs
import zipbap.app.admin.category.cookingtype.dto.CookingTypeRequestDto
import zipbap.app.admin.category.cookingtype.dto.CookingTypeResponseDto
import zipbap.app.admin.category.cookingtype.service.CookingTypeService
import zipbap.app.global.ApiResponse
import zipbap.app.global.code.status.SuccessStatus

@RestController
class CookingTypeController(
    private val cookingTypeService: CookingTypeService
) : CookingTypeDocs {

    override fun createCookingType(dto: CookingTypeRequestDto.CreateCookingTypeDto): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.of(SuccessStatus._OK, cookingTypeService.createCookingType(dto))

    override fun updateCookingType(id: Long, dto: CookingTypeRequestDto.UpdateCookingTypeDto): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.of(SuccessStatus._OK, cookingTypeService.updateCookingType(id, dto))

    override fun getCookingType(id: Long): ApiResponse<CookingTypeResponseDto> =
        ApiResponse.of(SuccessStatus._OK, cookingTypeService.getCookingType(id))

    override fun getAllCookingTypes(): ApiResponse<List<CookingTypeResponseDto>> =
        ApiResponse.of(SuccessStatus._OK, cookingTypeService.getAllCookingTypes())

    override fun deleteCookingType(id: Long): ApiResponse<Unit> =
        ApiResponse.of(SuccessStatus._OK, cookingTypeService.deleteCookingType(id))
}
