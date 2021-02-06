package com.taskforce.superinvention.common.config.argument.resolver.auth

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.common.exception.auth.UserNotFoundException
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

annotation class AuthUser(
    val allowAnonymous: Boolean = true
)

@Component
class AuthorizeArgumentResolver(
        val userRepository: UserRepository
): HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthUser::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): User? {
        val authUser = parameter.parameter.getAnnotation(AuthUser::class.java)

        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name

        val userEntity: User? = userRepository.findByUserId(userId)

        if(!authUser.allowAnonymous && userEntity == null) {
            throw UserNotFoundException()
        }

        return userEntity
    }
}