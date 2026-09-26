package zipbap.global.global.auth.resolver

import org.springframework.core.MethodParameter
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import zipbap.global.global.auth.domain.authuser.AuthUser
import zipbap.global.global.auth.domain.userdetails.CustomUserDetails

class UserInjectionArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        // Claim all annotated parameters so unsupported types cannot fall back to model binding.
        return parameter.hasParameterAnnotation(UserInjection::class.java)
    }

    @Throws(Exception::class)
    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        check(parameter.parameterType == Long::class.java || parameter.parameterType == Long::class.javaObjectType) {
            "@UserInjection must be declared on a Long parameter"
        }
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw AuthenticationCredentialsNotFoundException("Authentication required")
        }
        return when (val principal = authentication.principal) {
            is AuthUser -> principal.userId
            is CustomUserDetails -> principal.user.id
            else -> null
        } ?: throw AuthenticationCredentialsNotFoundException("Unsupported authentication principal")
    }
}
