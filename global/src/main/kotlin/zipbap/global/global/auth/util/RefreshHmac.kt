package zipbap.global.global.auth.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class RefreshHmac(
    @Value("\${spring.security.refresh.hmac-key}") private val hmacKeyB64: String
    ) {
        private val algorithm = "HmacSHA256"
        private lateinit var keySpec: SecretKeySpec

        init {
            val keyBytes = Base64.getUrlDecoder().decode(hmacKeyB64)
            require(keyBytes.size >= 32) { "HMAC key must be >= 32 bytes (256 bits)" }
            keySpec = SecretKeySpec(keyBytes, algorithm)
        }

        // Mac 인스턴스는 스레드세이프가 아니므로 매 호출마다 생성(가장 안전)
            fun hmacBase64Url(input: String): String {
                val mac = Mac.getInstance(algorithm).apply { init(keySpec) }
                val out = mac.doFinal(input.toByteArray(Charsets.UTF_8))
                return Base64.getUrlEncoder().withoutPadding().encodeToString(out)
            }

}