package com.taskforce.superinvention.common.config.security

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(request)

        try {
            if (!token.isBlank() && jwtTokenProvider.validateToken(token)) {
                val auth = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: BizException) {
            SecurityContextHolder.clearContext()
            response.sendError(ex.httpStatus.value(), ex.message)
            return
        }
        filterChain.doFilter(request, response)
    }
}