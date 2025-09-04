package zipbap.app.api.file.dto

import io.swagger.v3.oas.annotations.media.Schema

object PresignedUrlDto {

    data class PresignedUrlRequest(
        @Schema(description = "파일명", example = "step1.jpg")
        val fileName: String,

        @Schema(description = "저장 디렉토리", example = "recipes/temp")
        val dirName: String
    )

    data class PresignedUrlResponse(
        @Schema(description = "S3 업로드에 사용할 Presigned URL")
        val uploadUrl: String,

        @Schema(description = "업로드 완료 후 접근 가능한 파일 URL")
        val fileUrl: String
    )
}
