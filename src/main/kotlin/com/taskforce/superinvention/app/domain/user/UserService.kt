package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRole.UserRoleService
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.kakao.KakaoOAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import javax.transaction.Transactional

@Service
class UserService(
        private var userRepository: UserRepository,
        private var userRoleService: UserRoleService,
        private var userStateService: UserStateService,
        private var userInterestService: UserInterestService,
        private var kakaoOAuth: KakaoOAuth,
        private var jwtTokenProvider: JwtTokenProvider
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }

    fun getKakaoUserInfo(user: User): KakaoUserInfo {
        val kakaoToken = KakaoToken(
                refresh_token = user.refreshToken,
                access_token  = user.accessToken
        )

        val token = kakaoOAuth.refreshIfTokenExpired(kakaoToken)

        // 토큰이 만료되었을 때
        if(token.access_token != kakaoToken.access_token) {
            KakaoOAuth.LOG.info("[TOKEN EXPIRE] - ${user.userId}의 카카오 토큰이 만료되어 새로 갱신합니다.")
            updateUserToken(user, token)
        }

        return kakaoOAuth.getKakaoUserProfile(token)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun updateUserToken(user: User, token: KakaoToken) {
        val targetUser = userRepository.findByUserId(user.userId)!!
        targetUser.accessToken = token.access_token

        if(token.refresh_token?.isNotBlank()!!) {
            targetUser.refreshToken = token.refresh_token
        }

        userRepository.save(targetUser)
    }

    @Transactional(rollbackOn = [Exception::class])
    fun saveKakaoToken(kakaoToken: KakaoToken): AppToken {
        val token = kakaoOAuth.refreshIfTokenExpired(kakaoToken)
        val kakaoId = kakaoOAuth.getKakaoUserProfile(token).id

        // [1] kakao 유저 존재 x
        if(kakaoId.isBlank()) {
            log.error("unknown kakao token received")
            throw IllegalArgumentException()
        }

        var user: User? = userRepository.findByUserId(kakaoId)
        var isRegistered = false

        // [2] 유저 최초 가입시
        if (user == null) {
            user = User(kakaoId, token)
            userRepository.save(user)
            userRoleService.addRoleToUser(user, "UNREGISTERED")
        }

        if(user.isRegistered != 0) {
            isRegistered = true
        }

        // 토큰이 만료되었을 때
        if(token.access_token != kakaoToken.access_token) {
            KakaoOAuth.LOG.info("[TOKEN EXPIRE] - ${user.userId}의 카카오 토큰이 만료되어 새로 갱신합니다.")
            updateUserToken(user, token)
        }

        return AppToken(
                isRegistered,
                jwtTokenProvider.createAppToken(user.userId)
        )
    }

    @Transactional(rollbackOn = [Exception::class])
    fun registerUser(request: KakaoUserRegistRequest, user: User) {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink
        user.isRegistered = 1

        val userStates    = request.userStates
        val userInterests = request.userInterests

        userRepository.save(user)

        userRoleService.addRoleToUser(user, "USER")
        userRoleService.removeRoleFromUser(user, "UNREGISTERED")

        userStateService.changeUserState(user, userStates)
        userInterestService.changeUserInterest(user, userInterests)
    }
}