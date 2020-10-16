package com.taskforce.superinvention.app.domain.user.userRegion

import com.taskforce.superinvention.app.domain.region.RegionRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionWithPriorityDto
import com.taskforce.superinvention.app.web.dto.user.UserRegionDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegionService(
        private val userRegionRepository: UserRegionRepository,
        private val regionRepository: RegionRepository
) {

    fun findUserRegionList(user: User): UserRegionDto {
        val userRegions: List<UserRegion> = userRegionRepository.findByUserSeq(user.seq!!)

        return when(userRegions.isEmpty()) {
            true -> UserRegionDto(user, emptyList())
            else -> UserRegionDto(user, userRegions.map { e -> RegionWithPriorityDto(SimpleRegionDto(e.region), e.priority) }.toList())
        }
    }

    @Transactional
    fun changeUserRegion(user: User, regions: List<RegionRequestDto>): UserRegionDto {
        if (user.seq == null) throw NullPointerException()
        val findByUserSeq: List<UserRegion> = userRegionRepository.findByUserSeq(user.seq!!)
        userRegionRepository.deleteAll(findByUserSeq)

        val toAdd = regions.map { e -> UserRegion(user, regionRepository.findById(e.seq).orElseThrow { NullPointerException() }, e.priority) }.toMutableList()
        userRegionRepository.saveAll(toAdd)
        return findUserRegionList(user)
    }
}