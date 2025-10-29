package zipbap.app.admin.category.method.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.admin.category.method.dto.MethodRequestDto
import zipbap.admin.category.method.dto.MethodResponseDto
import zipbap.admin.category.method.service.MethodService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/methods")
@Tag(name = "Cooking Methods", description = "요리 방법 관리 API")
class MethodController(
    private val methodService: MethodService
) {

    @PostMapping
    fun createMethod(
        @RequestBody dto: MethodRequestDto.CreateMethodDto
    ): ApiResponse<MethodResponseDto> =
        ApiResponse.onSuccess(methodService.createMethod(dto))

    @PutMapping("/{id}")
    fun updateMethod(
        @PathVariable("id") id: Long,
        @RequestBody dto: MethodRequestDto.UpdateMethodDto
    ): ApiResponse<MethodResponseDto> =
        ApiResponse.onSuccess(methodService.updateMethod(id, dto))

    @GetMapping("/{id}")
    fun getMethod(
        @PathVariable("id") id: Long
    ): ApiResponse<MethodResponseDto> =
        ApiResponse.onSuccess(methodService.getMethod(id))

    @GetMapping
    fun getAllMethods(): ApiResponse<List<MethodResponseDto>> =
        ApiResponse.onSuccess(methodService.getAllMethods())

    @DeleteMapping("/{id}")
    fun deleteMethod(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(methodService.deleteMethod(id))
}
