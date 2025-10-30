package zipbap.global.global.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.*

/**
 * ✅ 요청(Request) + 응답(Response) 로깅 필터
 * - URI, 메서드, 요청 바디, 응답 상태 및 바디 출력
 * - 개발/테스트 환경에서만 사용하는 걸 권장
 */
@Component
class RequestResponseLoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cachedRequest = RequestWrapper(request)
        val cachedResponse = ResponseWrapper(response)

        val uri = cachedRequest.requestURI
        val method = cachedRequest.method
        val requestBody = cachedRequest.reader.readText().takeIf { it.isNotBlank() } ?: "(empty)"

        println("📥 [REQUEST] $method $uri")
        println("├─ Query: ${cachedRequest.queryString ?: "(none)"}")
        println("└─ Body: $requestBody")

        filterChain.doFilter(cachedRequest, cachedResponse)

        val responseBody = cachedResponse.copyBodyToResponse().takeIf { it.isNotBlank() } ?: "(empty)"

        println("📤 [RESPONSE] $method $uri (${cachedResponse.status})")
        println("└─ Body: $responseBody")
    }
}

/** 요청 바디를 복제하는 래퍼 */
class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val cachedBody: ByteArray = request.inputStream.readAllBytes()

    override fun getInputStream(): ServletInputStream {
        val inputStream = ByteArrayInputStream(cachedBody)
        return object : ServletInputStream() {
            override fun read(): Int = inputStream.read()
            override fun isFinished() = inputStream.available() == 0
            override fun isReady() = true
            override fun setReadListener(readListener: ReadListener?) {}
        }
    }

    override fun getReader(): BufferedReader = BufferedReader(InputStreamReader(inputStream))
}

/** 응답 바디를 복제하는 래퍼 */
class ResponseWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
    private val capture = ByteArrayOutputStream()
    private var outputStream: ServletOutputStream? = null
    private var writer: PrintWriter? = null

    override fun getOutputStream(): ServletOutputStream {
        if (outputStream == null) {
            outputStream = object : ServletOutputStream() {
                override fun isReady() = true
                override fun setWriteListener(listener: WriteListener?) {}
                override fun write(b: Int) = capture.write(b)
            }
        }
        return outputStream!!
    }

    override fun getWriter(): PrintWriter {
        if (writer == null) {
            writer = PrintWriter(OutputStreamWriter(capture, characterEncoding))
        }
        return writer!!
    }

    fun copyBodyToResponse(): String {
        writer?.flush()
        val content = capture.toString(characterEncoding)
        response.outputStream.write(capture.toByteArray())
        response.outputStream.flush()
        return content
    }
}
