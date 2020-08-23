package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.domain.user.userRole.UserRoleRepository
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.KakaoOAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.transaction.Transactional

@Service
class UserService(
        private var userRepository: UserRepository,
        private var userRoleRepository: UserRoleRepository,
        private var stateService: StateService,
        private var interestService: InterestService,
        private var kakaoOAuth: KakaoOAuth,
        private var jwtTokenProvider: JwtTokenProvider
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }

    fun getUserInfo(userId: String): User? {
        return userRepository.findByUserId(userId)
    }

    fun getKakaoUserInfo(user: User): KakaoUserInfo {
        return kakaoOAuth.getKakaoUserProfile(user.accessToken!!)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun publishAppToken(token: KakaoToken): AppToken {
        val kakaoId = kakaoOAuth.getKakaoId(token)

        if(kakaoId.isBlank()) {
            log.error("unknown kakao token received")
            throw IllegalArgumentException()
        }

        var user: User? = userRepository.findByUserId(kakaoId)
        var isFirst = false

        if (user == null) {
            isFirst = true
            user = User(kakaoId, token)
            userRepository.save(user)
            userRoleRepository.save(UserRole(user, "USER"))
        }

        return AppToken (
                isFirst,
                jwtTokenProvider.createAppToken(user.userId, user.userRoles)
        )
    }

    @Transactional
    fun registerUser(request: KakaoUserRegistRequest, user: User) {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink

        userRepository.save(user)

        val userStates = request.userStates
        stateService.changeUserState(user, userStates)
        val userInterests = request.userInterests

        interestService.changeUserInterest(user, userInterests)
    }
}