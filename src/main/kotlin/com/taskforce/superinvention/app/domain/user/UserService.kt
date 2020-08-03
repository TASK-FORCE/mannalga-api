package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.KakaoOAuth
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
        private var userRepository: UserRepository,
        private var userRoleRepository: UserRoleRepository,
        private var kakaoOAuth: KakaoOAuth,
        private var jwtTokenProvider: JwtTokenProvider
) {
    companion object {
        const val KAKAO_USER_URI = "https://kapi.kakao.com/v2/user/me"
    }

    fun getUserInfo(userId: String): User? {
        return userRepository.findByUserId(userId)
    }

    @Transactional
    fun publishAppToken(token: KakaoTokenDto): AppToken {
        val kakaoId = kakaoOAuth.getKakaoId(token)
        var user: User? = userRepository.findByUserId(kakaoId)
        var isFirst = false

        if (user == null) {
            isFirst = true
            user = User(kakaoId)
            userRepository.save(user)
            userRoleRepository.save(UserRole(user, "ROLE_USER"))
        }

        return AppToken(
                isFirst,
                jwtTokenProvider.createToken(user.userId, user.userRoles)
        )
    }
}