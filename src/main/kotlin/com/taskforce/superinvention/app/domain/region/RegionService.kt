package com.taskforce.superinvention.app.domain.region

import com.taskforce.superinvention.app.web.dto.region.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
class RegionService(
        val regionRepository: RegionRepository
) {
    @Transactional
    fun findAllRegionDtoList(): List<RegionDto> {
        return regionRepository.findByLevel(1).map { e -> of(e, 1) }.toList()
    }

    fun findBySeq(seq: Long): Region = regionRepository.findById(seq).orElseThrow{IllegalArgumentException()}
}