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
    private val userDetailsProvider: UserDetailsProvider,
) {
    @Value("\${security.jwt.token.secretKey}")
    lateinit var secretKey: String

    @Value("\${security.jwt.token.expireMinute}")
    lateinit var expireMinute: String

    companion object {
        const val TOKEN_HEADER  = "Authorization"
        const val TIME_ZONE_KST = "Asia/Seoul"
    }

    fun createAppToken(userId: String?): String {
        val payloads: Claims  = Jwts.claims()
        val now = LocalDateTime.now().atZone(ZoneId.of(TIME_ZONE_KST))

        val issuedDate  = Date.from(now.toInstant())
        val expiredDate = Date.from(now.plusMinutes(expireMinute.toLong()).toInstant() )

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
        Jwts.parser()
            .setSigningKey(secretKey.toByteArray())
            .parseClaimsJws(token)

        return  true
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
