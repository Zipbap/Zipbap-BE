package zipbap.global.security

import jakarta.servlet.FilterChain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import zipbap.global.global.config.RequestResponseLoggingFilter

@ExtendWith(OutputCaptureExtension::class)
class RequestLoggingSecurityTest {
    @Test fun `tokens and bodies are not logged or consumed`(output: CapturedOutput) {
        val request = MockHttpServletRequest("POST", "/api/auth/kakao/login")
        request.queryString = "accessToken=query-secret-sentinel"
        request.addHeader("Authorization", "Bearer header-secret-sentinel")
        request.setContent("request-secret-sentinel".toByteArray())
        val response = MockHttpServletResponse()
        RequestResponseLoggingFilter().doFilter(request, response, FilterChain { req, res ->
            assertThat(req.inputStream.readAllBytes().decodeToString()).isEqualTo("request-secret-sentinel")
            res.writer.write("response-secret-sentinel")
        })
        assertThat(response.contentAsString).isEqualTo("response-secret-sentinel")
        assertThat(output.all).doesNotContain("secret-sentinel")
    }
}
