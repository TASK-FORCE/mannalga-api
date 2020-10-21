package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoInterestDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoRegionDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInfoService(
        private val userRepository: UserRepository,
        private val userInterestService : UserInterestService,
        private val userRegionService: UserRegionService
){

    @Transactional
    fun getUserInfo(userData: User): UserInfoDto {

        val user = userRepository.findByUserId(userData.userId)!!

        // [1] 유저 관심사 조회
        val userInfoInterests: List<UserInfoInterestDto> = userInterestService.findUserInterests(user)

        // [2] 유저 관심 지역 조회
        val userRegions: List<UserInfoRegionDto> = userRegionService.findUserRegionList(user)
                .userRegions
                .map { userRegions -> UserInfoRegionDto(userRegions.priority, userRegions.region) }

        return UserInfoDto(user, userRegions, userInfoInterests)
    }
}