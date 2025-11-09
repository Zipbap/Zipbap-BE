package zipbap.user.api.mypage.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "마이페이지용 페이징 응답 DTO")
data class PageResponse<T>(
    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    val page: Int,

    @Schema(description = "페이지당 데이터 개수", example = "21")
    val size: Int,

    @Schema(description = "전체 페이지 수", example = "3")
    val totalPages: Int,

    @Schema(description = "전체 데이터 수", example = "55")
    val totalElements: Long,

    @Schema(description = "현재 페이지 데이터 목록")
    val content: List<T>
)
