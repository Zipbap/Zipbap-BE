package zipbap.user.api.file.controller

import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.file.docs.FileDocs
import zipbap.user.api.file.dto.PresignedUrlDto
import zipbap.global.global.cloud.service.PresignedUrlProvider
import zipbap.global.domain.file.FileEntity
import zipbap.global.domain.file.FileRepository
import zipbap.global.domain.file.FileStatus
import org.springframework.transaction.annotation.Transactional

@RestController
class FileController(
        private val presignedUrlProvider: PresignedUrlProvider,
        private val fileRepository: FileRepository
) : FileDocs {

    @Transactional
    override fun generatePresignedUrl(
        request: PresignedUrlDto.PresignedUrlRequest
    ): PresignedUrlDto.PresignedUrlResponse {
        val userId = 1L // TODO: JWT 적용 후 SecurityContext에서 추출
        val result = presignedUrlProvider.generateUploadUrl(userId, request.fileName)

        // DB에 TEMPORARY_UPLOAD 기록
        fileRepository.save(FileEntity(fileUrl = result["fileUrl"]!!, status = FileStatus.TEMPORARY_UPLOAD))

        return PresignedUrlDto.PresignedUrlResponse(
            uploadUrl = result["uploadUrl"]!!,
            fileUrl = result["fileUrl"]!!
        )
    }
}
