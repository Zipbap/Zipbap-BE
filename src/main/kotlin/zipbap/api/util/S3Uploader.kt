package zipbap.api.util

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class S3Uploader(
    private val amazonS3: AmazonS3,
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
        val fileName = "$dirName/${UUID.randomUUID()}-${file.originalFilename}"

        val metadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        file.inputStream.use { inputStream ->
            amazonS3.putObject(bucket, fileName, inputStream, metadata)
        }

        return amazonS3.getUrl(bucket, fileName).toString()
    }

    /**
     * S3에 업로드된 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 S3 객체의 전체 URL
     */
    fun deleteFile(fileUrl: String) {
        val key = extractKeyFromUrl(fileUrl)
        amazonS3.deleteObject(bucket, key)
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
