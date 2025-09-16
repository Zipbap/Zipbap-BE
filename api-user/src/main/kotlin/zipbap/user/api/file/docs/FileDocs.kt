package zipbap.user.api.file.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import zipbap.user.api.file.dto.PresignedUrlDto

@RequestMapping("/api/files")
interface FileDocs {

    @Operation(
        summary = "Presigned URL 발급",
        description = """
            프론트가 직접 S3에 업로드할 수 있는 URL을 발급합니다.
            
            ✅ 사용 흐름
            1. 이 API 호출 → uploadUrl, fileUrl 발급
            2. 프론트에서 uploadUrl로 PUT 요청 (파일 업로드)
            3. 업로드 성공 후 fileUrl을 백엔드 레시피 API에 전달
        """
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Presigned URL 발급 성공",
            content = [Content(schema = Schema(implementation = PresignedUrlDto.PresignedUrlResponse::class))]
        )
    )
    @PostMapping("/presigned-url")
    fun generatePresignedUrl(
        @RequestBody request: PresignedUrlDto.PresignedUrlRequest
    ): PresignedUrlDto.PresignedUrlResponse
}
