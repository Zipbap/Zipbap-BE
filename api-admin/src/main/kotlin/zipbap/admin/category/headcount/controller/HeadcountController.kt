package zipbap.admin.category.headcount.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.admin.category.headcount.dto.HeadcountRequestDto
import zipbap.admin.category.headcount.dto.HeadcountResponseDto
import zipbap.admin.category.headcount.service.HeadcountService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/headcounts")
@Tag(name = "Headcounts", description = "인분 관리 API")
class HeadcountController(
    private val headcountService: HeadcountService
) {

    @PostMapping
    fun createHeadcount(
        @RequestBody dto: HeadcountRequestDto.CreateHeadcountDto
    ): ApiResponse<HeadcountResponseDto> =
        ApiResponse.onSuccess(headcountService.createHeadcount(dto))

    @PutMapping("/{id}")
    fun updateHeadcount(
        @PathVariable("id") id: Long,
        @RequestBody dto: HeadcountRequestDto.UpdateHeadcountDto
    ): ApiResponse<HeadcountResponseDto> =
        ApiResponse.onSuccess(headcountService.updateHeadcount(id, dto))

    @GetMapping("/{id}")
    fun getHeadcount(
        @PathVariable("id") id: Long
    ): ApiResponse<HeadcountResponseDto> =
        ApiResponse.onSuccess(headcountService.getHeadcount(id))

    @GetMapping
    fun getAllHeadcounts(): ApiResponse<List<HeadcountResponseDto>> =
        ApiResponse.onSuccess(headcountService.getAllHeadcounts())

    @DeleteMapping("/{id}")
    fun deleteHeadcount(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(headcountService.deleteHeadcount(id))
}
