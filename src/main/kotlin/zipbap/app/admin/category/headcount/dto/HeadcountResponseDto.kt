package zipbap.app.admin.category.headcount.dto

@io.swagger.v3.oas.annotations.media.Schema(description = "인분 응답 DTO")
data class HeadcountResponseDto(
    @io.swagger.v3.oas.annotations.media.Schema(description = "ID", example = "1")
    val id: Long,

    @io.swagger.v3.oas.annotations.media.Schema(description = "인분 명칭", example = "2인분")
    val headcount: String
)