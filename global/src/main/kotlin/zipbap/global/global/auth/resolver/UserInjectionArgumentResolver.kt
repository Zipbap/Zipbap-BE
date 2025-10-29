package zipbap.global.global.auth.resolver

import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import zipbap.global.domain.user.User
import zipbap.global.global.auth.domain.userdetails.CustomUserDetails

class UserInjectionArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(UserInjection::class.java) && parameter.parameterType == User::class.java
    }

    @Throws(Exception::class)
    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication

        if (!authentication.isAuthenticated) {
            return null
        }

        val principal = authentication.principal

        if (principal is CustomUserDetails) {
            return principal.user
        }

        return null
    }
}
