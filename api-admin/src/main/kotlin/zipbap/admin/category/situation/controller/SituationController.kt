package zipbap.admin.category.situation.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.admin.category.situation.dto.SituationRequestDto
import zipbap.admin.category.situation.dto.SituationResponseDto
import zipbap.admin.category.situation.service.SituationService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/situations")
@Tag(name = "Cooking situations", description = "요리 상황 관리 API")
class SituationController(
    private val situationService: SituationService
) {

    @PostMapping
    fun createSituation(
        @RequestBody dto: SituationRequestDto.CreateSituationDto
    ): ApiResponse<SituationResponseDto> =
        ApiResponse.onSuccess(situationService.createSituation(dto))

    @PutMapping("/{id}")
    fun updateSituation(
        @PathVariable("id") id: Long,
        @RequestBody dto: SituationRequestDto.UpdateSituationDto
    ): ApiResponse<SituationResponseDto> =
        ApiResponse.onSuccess(situationService.updateSituation(id, dto))

    @GetMapping("/{id}")
    fun getSituation(
        @PathVariable("id") id: Long
    ): ApiResponse<SituationResponseDto> =
        ApiResponse.onSuccess(situationService.getSituation(id))

    @GetMapping
    fun getAllSituations(): ApiResponse<List<SituationResponseDto>> =
        ApiResponse.onSuccess(situationService.getAllSituations())

    @DeleteMapping("/{id}")
    fun deleteSituation(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(situationService.deleteSituation(id))
}
