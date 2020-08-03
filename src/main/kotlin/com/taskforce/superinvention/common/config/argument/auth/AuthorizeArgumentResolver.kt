package com.taskforce.superinvention.common.config.argument.auth

import com.taskforce.superinvention.common.config.security.SecurityUser
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthorizeArgumentResolver: HandlerMethodArgumentResolver{

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter == SecurityUser::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): SecurityUser {
        return SecurityContextHolder.getContext().authentication.principal as SecurityUser
    }
}