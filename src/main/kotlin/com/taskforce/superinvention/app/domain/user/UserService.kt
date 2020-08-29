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

    fun getKakaoUserInfo(user: User): KakaoUserInfo {
        return kakaoOAuth.getKakaoUserProfile(user.accessToken!!)
    }

    @Transactional
    fun saveKakaoToken(token: KakaoToken): AppToken {
        val kakaoId = kakaoOAuth.getKakaoUserId(token)

        if(kakaoId.isBlank()) {
            log.error("unknown kakao token received")
            throw IllegalArgumentException()
        }

        var user: User? = userRepository.findByUserId(kakaoId)
        var isRegistered = false

        if (user == null) {
            user = User(kakaoId, token)
            userRepository.save(user)
            userRoleRepository.save(UserRole(user, "UNREGISTERED"))
        }

        if(user.isRegistered != 0) {
            isRegistered = true
        }

        return AppToken(
                isRegistered,
                jwtTokenProvider.createAppToken(user.userId)
        )
    }

//    @Transactional
//    fun getAppToken(): AppToken{
//
//    }

    @Transactional
    fun registerUser(request: KakaoUserRegistRequest, user: User) {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink
        user.isRegistered = 1

        val userStates    = request.userStates
        val userInterests = request.userInterests

        userRepository.save(user)
        userRoleRepository.save(UserRole(user, "USER"))
        stateService.changeUserState(user, userStates)
        interestService.changeUserInterest(user, userInterests)
    }
}