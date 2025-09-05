package zipbap.app.api.file.dto

import io.swagger.v3.oas.annotations.media.Schema

class PresignedUrlDto {

    data class PresignedUrlRequest(
        @Schema(description = "파일명", example = "food.png")
        val fileName: String
    )

    data class PresignedUrlResponse(
        @Schema(description = "업로드용 Presigned URL")
        val uploadUrl: String,

        @Schema(description = "업로드된 파일 접근 URL")
        val fileUrl: String
    )
}
