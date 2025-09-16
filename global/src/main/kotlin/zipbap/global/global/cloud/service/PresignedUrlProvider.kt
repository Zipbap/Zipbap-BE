package zipbap.global.global.cloud.service

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
     * Presigned URL 생성 (임시 업로드 전용)
     */
    fun generateUploadUrl(userId: Long, fileName: String, expirationMinutes: Int = 5): Map<String, String> {
        val key = "temp/$userId/${UUID.randomUUID()}-$fileName"
        val expiration = Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000)

        val uploadUrl = amazonS3.generatePresignedUrl(bucket, key, expiration, HttpMethod.PUT)
        val fileUrl = amazonS3.getUrl(bucket, key).toString()

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

        return amazonS3.getUrl(bucket, targetKey).toString()
    }

    private fun extractKeyFromUrl(fileUrl: String): String {
        val urlPrefix = amazonS3.getUrl(bucket, "").toString()
        return fileUrl.removePrefix(urlPrefix)
    }
}
