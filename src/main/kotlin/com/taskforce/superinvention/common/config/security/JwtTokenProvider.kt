package com.taskforce.superinvention.common.config.security

import com.taskforce.superinvention.app.domain.user.UserDetailsProvider
import com.taskforce.superinvention.common.exception.BizException
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Component
class JwtTokenProvider(
    private val userDetailsProvider: UserDetailsProvider
) {

    companion object {

        const val TOKEN_HEADER  = "Authorization"
        const val TIME_ZONE_KST = "Asia/Seoul"

        @Value("\${security.jwt.token.secret-key}")
        var secretKey: String = "dev-secret-key"

        @Value("\${security.jwt.token.expire-day}")
        var expireDay: Long = 365
    }

    fun createAppToken(userId: String): String {
        val payloads: Claims  = Jwts.claims()
        val now = LocalDateTime.now().atZone(ZoneId.of(TIME_ZONE_KST))

        val issuedDate  = Date.from(now.toInstant())
        val expiredDate = Date.from(now.plusDays(expireDay).toInstant() )

        payloads["userId"] = userId

        return Jwts.builder()
            .setClaims(payloads)
            .setSubject(userId)
            .setIssuedAt(issuedDate)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS256, secretKey.toByteArray())
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userId = getUserId(token)
        val userDetails = userDetailsProvider.loadUserByUsername(userId)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun validateToken(token: String?): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(secretKey.toByteArray())
                .parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            throw BizException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: IllegalArgumentException) {
            throw BizException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun getUserId(token: String): String {
        return Jwts
                .parser()
                .setSigningKey(secretKey.toByteArray())
                .parseClaimsJws(token)
                .body
                .subject
    }
}