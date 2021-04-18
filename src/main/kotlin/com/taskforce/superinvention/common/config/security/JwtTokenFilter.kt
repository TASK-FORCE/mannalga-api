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
        val token: String = resolveToken(request)

        try {
            if (isTokenValidate(token)) {
                val auth: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: BizException) {
            SecurityContextHolder.clearContext()
            response.sendError(ex.httpStatus.value(), ex.message)
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(req: HttpServletRequest): String {
        val bearerToken = req.getHeader(JwtTokenProvider.TOKEN_HEADER)
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else ""
    }

    private fun isTokenValidate(token: String): Boolean {
        if(token.isBlank()) {
            return false
        }

        if(jwtTokenProvider.validateToken(token)) {
            return true
        }
        return false
    }
}