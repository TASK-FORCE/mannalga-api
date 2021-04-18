package com.taskforce.superinvention.common.config.security

import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerExceptionResolver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class FilterChainExceptionHandler : OncePerRequestFilter() {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    lateinit var resolver: HandlerExceptionResolver

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            resolver.resolveException(request, response, null, e)
        }
    }
}