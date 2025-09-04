package zipbap.app.admin.category.mainingredient.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.app.admin.category.mainingredient.dto.MainIngredientRequestDto
import zipbap.app.admin.category.mainingredient.dto.MainIngredientResponseDto
import zipbap.app.admin.category.mainingredient.service.MainIngredientService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/main-ingredients")
@Tag(name = "Main Ingredients", description = "요리 주재료 관리 API")
class MainIngredientController(
    private val mainIngredientService: MainIngredientService
) {

    @PostMapping
    fun createMainIngredient(
        @RequestBody dto: MainIngredientRequestDto.CreateMainIngredientDto
    ): ApiResponse<MainIngredientResponseDto> =
        ApiResponse.onSuccess(mainIngredientService.createMainIngredient(dto))

    @PutMapping("/{id}")
    fun updateMainIngredient(
        @PathVariable("id") id: Long,
        @RequestBody dto: MainIngredientRequestDto.UpdateMainIngredientDto
    ): ApiResponse<MainIngredientResponseDto> =
        ApiResponse.onSuccess(mainIngredientService.updateMainIngredient(id, dto))

    @GetMapping("/{id}")
    fun getMainIngredient(
        @PathVariable("id") id: Long
    ): ApiResponse<MainIngredientResponseDto> =
        ApiResponse.onSuccess(mainIngredientService.getMainIngredient(id))

    @GetMapping
    fun getAllMainIngredients(): ApiResponse<List<MainIngredientResponseDto>> =
        ApiResponse.onSuccess(mainIngredientService.getAllMainIngredients())

    @DeleteMapping("/{id}")
    fun deleteMainIngredient(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(mainIngredientService.deleteMainIngredient(id))
}
