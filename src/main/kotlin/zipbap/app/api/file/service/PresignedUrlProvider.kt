package zipbap.app.api.file.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class PresignedUrlProvider(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {

    /**
     * Presigned URL 생성
     *
     * @param key 업로드할 파일 경로
     * @param expirationMinutes URL 만료 시간 (기본 5분)
     * @return 업로드 URL, 파일 접근 URL
     */
    fun generateUploadUrl(key: String, expirationMinutes: Int = 5): Map<String, String> {
        val expiration = Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000)

        val uploadUrl = amazonS3.generatePresignedUrl(bucket, key, expiration, HttpMethod.PUT)
        val fileUrl = amazonS3.getUrl(bucket, key).toString()

        return mapOf(
            "uploadUrl" to uploadUrl.toString(),
            "fileUrl" to fileUrl
        )
    }
}
