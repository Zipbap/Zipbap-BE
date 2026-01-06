package zipbap.global.global.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExecutionTimeConfig {

    @Bean
    @ConditionalOnProperty(
            prefix = "feature.execution-time-filter",
            name = ["enabled"],
            havingValue = "true",
            matchIfMissing = false
    )
    fun executionTimeFilter(): ApiExecutionTimeFilter {
        return ApiExecutionTimeFilter()
    }
}

class ApiExecutionTimeFilter : Filter {

    private val log = LoggerFactory.getLogger(ApiExecutionTimeFilter::class.java)

    override fun doFilter(
            request: ServletRequest,
            response: ServletResponse,
            chain: FilterChain
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        // 요청 시작 시간 기록
        val startTime = System.currentTimeMillis()

        try {
            // 다음 필터 또는 서블릿으로 요청 전달
            chain.doFilter(request, response)
        } finally {
            // 요청 종료 시간 기록 및 소요 시간 계산
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime

            // 로그 출력
            log.info(
                    "[API 실행 시간] {} {} - {}ms (Status: {})",
                    httpRequest.method,
                    httpRequest.requestURI,
                    executionTime,
                    httpResponse.status
            )
        }
    }
}