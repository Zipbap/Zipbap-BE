package zipbap.admin.category.cookingtime.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.admin.category.cookingtime.dto.CookingTimeRequestDto
import zipbap.admin.category.cookingtime.dto.CookingTimeResponseDto
import zipbap.admin.category.cookingtime.service.CookingTimeService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/cooking-times")
@Tag(name = "Cooking Times", description = "요리 시간 관리 API")
class CookingTimeController(
    private val cookingTimeService: CookingTimeService
) {

    @PostMapping
    fun createCookingTime(
        @RequestBody dto: CookingTimeRequestDto.CreateCookingTimeDto
    ): ApiResponse<CookingTimeResponseDto> =
        ApiResponse.onSuccess(cookingTimeService.createCookingTime(dto))

    @PutMapping("/{id}")
    fun updateCookingTime(
        @PathVariable("id") id: Long,
        @RequestBody dto: CookingTimeRequestDto.UpdateCookingTimeDto
    ): ApiResponse<CookingTimeResponseDto> =
        ApiResponse.onSuccess(cookingTimeService.updateCookingTime(id, dto))

    @GetMapping("/{id}")
    fun getCookingTime(
        @PathVariable("id") id: Long
    ): ApiResponse<CookingTimeResponseDto> =
        ApiResponse.onSuccess(cookingTimeService.getCookingTime(id))

    @GetMapping
    fun getAllCookingTimes(): ApiResponse<List<CookingTimeResponseDto>> =
        ApiResponse.onSuccess(cookingTimeService.getAllCookingTimes())

    @DeleteMapping("/{id}")
    fun deleteCookingTime(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(cookingTimeService.deleteCookingTime(id))
}
