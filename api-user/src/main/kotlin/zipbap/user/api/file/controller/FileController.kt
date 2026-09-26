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
        private val fileRepository: FileRepository,
        private val userRepository: zipbap.global.domain.user.UserRepository
) : FileDocs {

    @Transactional
    override fun generatePresignedUrl(
        userId: Long,
        request: PresignedUrlDto.PresignedUrlRequest
    ): PresignedUrlDto.PresignedUrlResponse {
        val uploader = userRepository.findById(userId).orElseThrow {
            zipbap.global.global.exception.GeneralException(zipbap.global.global.code.status.ErrorStatus.USER_NOT_FOUND)
        }
        val result = presignedUrlProvider.generateUploadUrl(userId, request.fileName)

        // DB에 TEMPORARY_UPLOAD 기록
        fileRepository.save(FileEntity(fileUrl = result["fileUrl"]!!, status = FileStatus.TEMPORARY_UPLOAD, uploader = uploader))

        return PresignedUrlDto.PresignedUrlResponse(
            uploadUrl = result["uploadUrl"]!!,
            fileUrl = result["fileUrl"]!!
        )
    }
}
