package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.web.dto.AppToken
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.app.web.dto.KakaoUserDto
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userRoleRepository: UserRoleRepository,
        private val jwtTokenProvider: JwtTokenProvider,
        private val restTemplate: RestTemplate
){
    companion object {
        const val KAKAO_USER_URI = "https://kapi.kakao.com/v2/user/me"
    }

    fun getUserInfo(userId: String): User? {
        return userRepository.findByUserId(userId)
    }

    fun registerUserWithToken(token: KakaoTokenDto) : AppToken {
        val kakaoId = getKakaoId(token)
        var user = userRepository.findByUserId(kakaoId)
        var isFirst = false

        if(user == null) {
            isFirst = true
            user = User(userId = kakaoId, userType = UserType.KAKAO)
            userRepository.save(user)
            userRoleRepository.save(UserRole(user, "ROLE_USER"))
        }

        return AppToken(
                isFirst,
                jwtTokenProvider.createToken(user.userId, user.userRoles)
        )
    }

    private fun getKakaoId(token: KakaoTokenDto): String {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${token.accessToken}")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val userProfileRequest = restTemplate.exchange(
                KAKAO_USER_URI,
            HttpMethod.GET,
            request,
            KakaoUserDto::class.java
        )

        return when(userProfileRequest.statusCode) {
            HttpStatus.OK -> userProfileRequest.body!!.id
            else -> ""
        }
//        return when(userProfileRequest.statusCode) {
//
//        }
//        return if(userProfileRequest.statusCode == HttpStatus.OK) {
//            userRepository.findByUserId(userProfileRequest.body!!.id)
//        } else {
//            // 401 unauthorized
//            null
//        }
    }
}