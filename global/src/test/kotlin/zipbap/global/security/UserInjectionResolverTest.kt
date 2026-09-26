package zipbap.global.security

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest
import zipbap.global.domain.user.User
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.global.global.auth.resolver.UserInjectionArgumentResolver

class UserInjectionResolverTest {
    @Suppress("UNUSED_PARAMETER")
    fun unsupported(@UserInjection user: User) = Unit

    @Test fun `unsupported annotated type is claimed and fails instead of request binding`() {
        val parameter = MethodParameter(javaClass.getMethod("unsupported", User::class.java), 0)
        val resolver = UserInjectionArgumentResolver()
        val request = MockHttpServletRequest().apply { addParameter("id", "999") }
        assertThat(resolver.supportsParameter(parameter)).isTrue()
        assertThatThrownBy { resolver.resolveArgument(parameter, null, ServletWebRequest(request), null) }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
