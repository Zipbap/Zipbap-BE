package zipbap.global.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import zipbap.global.global.cloud.service.PresignedUrlProvider

class PresignedUrlProviderTest {
    @Test fun `presign includes upload owner and expiry without contacting AWS`() {
        val credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create("test-key", "test-secret"))
        S3Client.builder().region(Region.AP_NORTHEAST_2).credentialsProvider(credentials).build().use { client ->
            S3Presigner.builder().region(Region.AP_NORTHEAST_2).credentialsProvider(credentials).build().use { signer ->
                val result = PresignedUrlProvider(signer, client, "test-bucket", "cdn.example.invalid")
                    .generateUploadUrl(123, "recipe.png")
                assertThat(result["uploadUrl"]).contains("temp/123/", "X-Amz-Expires=300", "X-Amz-Signature=")
                assertThat(result["fileUrl"]).contains("temp/123/")
            }
        }
    }
}
