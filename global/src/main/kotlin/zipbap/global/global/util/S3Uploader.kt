package zipbap.global.global.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 2026.03.05 부 V2로 update
 */
@Component
class S3Uploader(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {

    /**
     * MultipartFile을 S3에 업로드하고 접근 가능한 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @param dirName 버킷 내 디렉토리 이름 (예: "uploads")
     * @return 업로드된 파일의 전체 URL
     */
    @Throws(IOException::class)
    fun uploadFile(file: MultipartFile, dirName: String): String {
        val originalFilename = file.originalFilename ?: "unknown"
        val key = "$dirName/${UUID.randomUUID()}-$originalFilename"

        // 💡 1. 메타데이터와 업로드 정보를 담은 PutObjectRequest 생성
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .contentLength(file.size)
            .build()

        // 💡 2. S3에 파일 업로드 (RequestBody로 InputStream을 감싸서 전달)
        file.inputStream.use { inputStream ->
            val requestBody = RequestBody.fromInputStream(inputStream, file.size)
            s3Client.putObject(putObjectRequest, requestBody)
        }

        // 💡 3. 업로드된 파일의 URL 반환 (v2 유틸리티 활용)
        return s3Client.utilities().getUrl { it.bucket(bucket).key(key) }.toString()
    }


    /**
     * S3에 업로드된 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 S3 객체의 전체 URL
     */
    fun deleteFile(fileUrl: String) {
        val key = extractKeyFromUrl(fileUrl)

        // 💡 v2 방식의 DeleteObjectRequest 생성
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        // 💡 S3Client를 사용하여 삭제 수행
        s3Client.deleteObject(deleteObjectRequest)
    }

    /**
     * S3 URL에서 key 경로를 추출합니다.
     *
     * @param url S3 파일 URL
     * @return 버킷 내 key 경로
     */
    private fun extractKeyFromUrl(url: String): String {
        val s3Url = URL(url)
        var path = s3Url.path
        if (path.startsWith("/")) {
            path = path.substring(1)
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8)
    }
}
