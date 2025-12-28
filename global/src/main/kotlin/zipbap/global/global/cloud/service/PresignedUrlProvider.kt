package zipbap.global.global.cloud.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.util.*

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PresignedUrlProvider(
    private val amazonS3: AmazonS3,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,

    @Value("\${cloud.cdn.domain}")
    private val cdnDomain: String
) {

    /**
     * Presigned URL 생성 (업로드는 S3에, 조회는 CDN으로!)
     */
    fun generateUploadUrl(
        userId: Long,
        fileName: String,
        expirationMinutes: Int = 5
    ): Map<String, String> {

        val key = "temp/$userId/${UUID.randomUUID()}-$fileName"
        val expiration = Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000)

        // 1) 업로드 URL (S3 PUT 용) 그대로 유지
        val uploadUrl = amazonS3.generatePresignedUrl(bucket, key, expiration, HttpMethod.PUT)

        // 2) 조회 URL은 CloudFront CDN 도메인으로 생성
        val fileUrl = buildCdnUrl(key)

        return mapOf(
            "uploadUrl" to uploadUrl.toString(),
            "fileUrl" to fileUrl
        )
    }

    /**
     * temp → recipes 경로로 복사
     * temp 파일은 삭제하지 않음
     */
    fun copyTempToRecipe(fileUrl: String, recipeId: String): String {
        val sourceKey = extractKeyFromUrl(fileUrl)

        if (!sourceKey.startsWith("temp/")) {
            throw IllegalArgumentException("Source key must start with temp/: $sourceKey")
        }

        val targetKey = sourceKey.replaceFirst("temp/", "recipes/$recipeId/")
        amazonS3.copyObject(bucket, sourceKey, bucket, targetKey)

        // CloudFront URL 반환
        return buildCdnUrl(targetKey)
    }

    /**
     * CloudFront URL 또는 S3 URL에서 key 추출
     */
    private fun extractKeyFromUrl(url: String): String {
        return when {
            url.contains(cdnDomain) -> {
                // ex: https://dxxxxxxx.cloudfront.net/temp/1/xxx.jpg
                URL(url).path.removePrefix("/")
            }
            else -> {
                // 기존 S3 URL fallback
                val bucketUrlPrefix = amazonS3.getUrl(bucket, "").toString()
                url.removePrefix(bucketUrlPrefix)
            }
        }
    }

    private fun buildCdnUrl(key: String): String {
        return "https://$cdnDomain/$key"
    }
}
