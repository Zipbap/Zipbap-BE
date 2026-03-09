package zipbap.global.global.cloud.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration
import java.util.*

/**
 * 테스트 실행시에 버전 충돌 오류로 인해 AWS 버전을 업데이트 했습니다.
 * 아예 싹다 바껴있다고해서 현재 버전에 맞는 리팩토링 과정까지만 수행했고, 추후 실제 사용시 테스트 필요합니다.
 * 2026-03-05 by 이창준
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PresignedUrlProvider(
    private val s3Presigner: S3Presigner,
    private val s3Client: S3Client,

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
        expirationMinutes: Long = 5L
    ): Map<String, String> {

        val key = "temp/$userId/${UUID.randomUUID()}-$fileName"
//        val expiration = Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000)

        // 업로드할 파일의 메타데이터 설정 (v2 방식)
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        // Presigned URL 요청 객체 생성
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .putObjectRequest { putObjectRequest }
            .build()

        // 1) 업로드 URL (S3 PUT 용) 그대로 유지
        val uploadUrl = s3Presigner.presignPutObject { presignRequest }
            .url().toString()

        // 2) 조회 URL은 CloudFront CDN 도메인으로 생성
        val fileUrl = buildCdnUrl(key)

        return mapOf(
            "uploadUrl" to uploadUrl,
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
        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(bucket)
            .sourceKey(sourceKey)
            .destinationBucket(bucket)
            .destinationKey(targetKey)
            .build()

        s3Client.copyObject(copyRequest)

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
                val bucketUrlPrefix = s3Client.utilities()
                    .getUrl { it.bucket(bucket).key("") }
                    .toString()
                url.removePrefix(bucketUrlPrefix)
            }
        }
    }

    private fun buildCdnUrl(key: String): String {
        return "https://$cdnDomain/$key"
    }
}
