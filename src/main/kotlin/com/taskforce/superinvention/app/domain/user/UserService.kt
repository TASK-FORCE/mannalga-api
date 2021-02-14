package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRole.UserRoleService
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.user.UserIdAndNameDto
import com.taskforce.superinvention.app.web.dto.user.UserProfileUpdateDto
import com.taskforce.superinvention.common.config.security.AppToken
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.kakao.KakaoOAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
        private var userRepository: UserRepository,
        private var userRoleService: UserRoleService,
        private var userRegionService: UserRegionService,
        private var userInterestService: UserInterestService,
        private var kakaoOAuth: KakaoOAuth,
        private var jwtTokenProvider: JwtTokenProvider,
        private val awsS3Mo: AwsS3Mo
) {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserService::class.java)
        val cannotFindUserException = BizException("존재하지 않는 유저입니다")
    }

    fun getKakaoUserInfo(user: User): KakaoUserInfo {
        val kakaoToken = KakaoToken(
                refresh_token = user.refreshToken,
                access_token  = user.accessToken
        )

        val token = kakaoOAuth.refreshIfTokenExpired(kakaoToken)

        // 토큰이 만료되었을 때
        if(kakaoToken.access_token.isNullOrBlank() || token.access_token != kakaoToken.access_token) {
            LOG.info("[TOKEN EXPIRE] - ${user.userId}의 카카오 토큰이 만료되어 새로 갱신합니다.")
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
            LOG.error("unknown kakao token received")
            throw BizException("존재하지 않는 kakao userid입니다", HttpStatus.NOT_FOUND)
        }

        var user: User? = userRepository.findByUserId(kakaoId)

        // [2] 유저 최초 가입시
        if (user == null) {
            user = User(kakaoId, token)
            userRepository.save(user)
            userRoleService.addRole(user, Role.RoleName.NONE)
        }

        // 토큰이 만료되었을 때
        if(token.access_token != kakaoToken.access_token) {
            LOG.info("[TOKEN EXPIRE] - ${user.userId}의 카카오 토큰이 만료되어 새로 갱신합니다.")
            updateUserToken(user, token)
        }

        user.accessToken  = token.access_token
        user.refreshToken = token.refresh_token

        return AppToken(
                user.isRegistered,
                jwtTokenProvider.createAppToken(user.userId)
        )
    }

    @Transactional(rollbackOn = [Exception::class])
    fun registerUser(request: KakaoUserRegistRequest, user: User) {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink
        user.isRegistered = true

        val userRegions    = request.userRegions
        val userInterests = request.userInterests

        userRepository.save(user)

        userRoleService.addRole(user, Role.RoleName.MEMBER)
        userRoleService.removeRoleIfExist(user, Role.RoleName.NONE)

        userRegionService.changeUserRegion(user, userRegions)
        userInterestService.changeUserInterest(user, userInterests)
    }

    @Transactional
    fun getUserByUsername(username: String): User {
        var user = userRepository.findByUserName(username)
        if (user == null) throw cannotFindUserException
        return user
    }

    @Transactional
    fun updateUser(authUser: User, body: UserProfileUpdateDto): User {
        val user = userRepository.findByIdOrNull(authUser.seq!!)
            ?: throw cannotFindUserException

        val movedFile = awsS3Mo.moveFile(body.profileImage, "user-profile/${user.seq}/${body.profileImage.fileName}")
        user.profileImageLink = movedFile.absolutePath
        return user
    }

    fun getUserIdAndUserNameList(): List<UserIdAndNameDto> {
        return userRepository.findAll().filter { it.isRegistered }.map(::UserIdAndNameDto)
    }

    fun getUserBySeq(userSeq: Long): User {
        return userRepository.findByIdOrNull(userSeq)?: throw cannotFindUserException
    }
}