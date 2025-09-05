package zipbap.app.api.auth.domain.token.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import zipbap.app.global.ApiResponse
import java.io.IOException
import java.util.Map

@Component
class TokenResponseWriter(
    private val objectMapper: ObjectMapper
) {
    @Throws(IOException::class)
    fun write(response: HttpServletResponse, access: String, refresh: String) {
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val body = mapOf(
                "accessToken" to access,
                "refreshToken" to refresh)

        val jsonResponse = objectMapper.writeValueAsString(ApiResponse.onSuccess(body))
        response.writer.write(jsonResponse)
    }
}
