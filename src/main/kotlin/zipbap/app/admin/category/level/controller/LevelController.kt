package zipbap.app.admin.category.level.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.app.admin.category.level.dto.LevelRequestDto
import zipbap.app.admin.category.level.dto.LevelResponseDto
import zipbap.app.admin.category.level.service.LevelService
import zipbap.app.global.ApiResponse

@RestController
@RequestMapping("/admin/levels")
@Tag(name = "Levels", description = "난이도 관리 API")
class LevelController(
    private val levelService: LevelService
) {

    @PostMapping
    fun createLevel(
        @RequestBody dto: LevelRequestDto.CreateLevelDto
    ): ApiResponse<LevelResponseDto> =
        ApiResponse.onSuccess(levelService.createLevel(dto))

    @PutMapping("/{id}")
    fun updateLevel(
        @PathVariable("id") id: Long,
        @RequestBody dto: LevelRequestDto.UpdateLevelDto
    ): ApiResponse<LevelResponseDto> =
        ApiResponse.onSuccess(levelService.updateLevel(id, dto))

    @GetMapping("/{id}")
    fun getLevel(
        @PathVariable("id") id: Long
    ): ApiResponse<LevelResponseDto> =
        ApiResponse.onSuccess(levelService.getLevel(id))

    @GetMapping
    fun getAllLevels(): ApiResponse<List<LevelResponseDto>> =
        ApiResponse.onSuccess(levelService.getAllLevels())

    @DeleteMapping("/{id}")
    fun deleteLevel(
        @PathVariable("id") id: Long
    ): ApiResponse<Unit> =
        ApiResponse.onSuccess(levelService.deleteLevel(id))
}
