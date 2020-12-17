package com.taskforce.superinvention.app.domain.region

import com.taskforce.superinvention.app.web.dto.region.*
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
class RegionService(
        val regionRepository: RegionRepository
) {
    @Transactional
    fun findAllRegionDtoList(): List<RegionDto> {
        val regions = regionRepository.findByLevelWithSubRegions().map { of(it, it.subRegions) }
        return regions
    }

    fun findBySeq(seq: Long): Region = regionRepository.findById(seq).orElseThrow{IllegalArgumentException()}

    @Transactional
    fun checkBeforeConvertClubRegion(regions: Iterable<Region>): Iterable<Region> {
        if (!isInEqualsSuperRegion(regions)) throw BizException("하나의 도/(특별)시 에 속하는 지역 끼리만 등록할 수 있습니다", HttpStatus.BAD_REQUEST)
        return regions
    }

    @Transactional
    fun isInEqualsSuperRegion(regions: Iterable<Region>): Boolean {
        return regions.map { it.superRegion?: it }.distinctBy { it.seq }.size == 1
    }

    @Transactional
    fun findBySeqList(seqList: Iterable<Long>) = regionRepository.findAllById(seqList)
}