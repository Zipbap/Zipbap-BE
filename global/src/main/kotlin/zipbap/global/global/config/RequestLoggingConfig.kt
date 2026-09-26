package zipbap.global.global.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerMapping

/** Never capture headers, query strings or bodies: they can contain tokens, PII and signed URLs. */
@Component
class RequestResponseLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val started = System.nanoTime()
        try {
            chain.doFilter(request, response)
        } finally {
            val route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) ?: "unmapped"
            log.info("http method={} route={} status={} durationMs={}",
                request.method, route, response.status, (System.nanoTime() - started) / 1_000_000)
        }
    }
}
