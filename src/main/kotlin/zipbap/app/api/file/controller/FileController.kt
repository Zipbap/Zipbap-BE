package zipbap.app.api.file.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.file.docs.FileDocs
import zipbap.app.api.file.dto.PresignedUrlDto
import zipbap.app.api.file.service.PresignedUrlProvider
import java.util.*

@RestController
class FileController(
    private val presignedUrlProvider: PresignedUrlProvider
) : FileDocs {

    override fun generatePresignedUrl(
        request: PresignedUrlDto.PresignedUrlRequest
    ): PresignedUrlDto.PresignedUrlResponse {
        val key = "${request.dirName}/${UUID.randomUUID()}-${request.fileName}"
        val result = presignedUrlProvider.generateUploadUrl(key)
        return PresignedUrlDto.PresignedUrlResponse(
            uploadUrl = result["uploadUrl"]!!,
            fileUrl = result["fileUrl"]!!
        )
    }
}
