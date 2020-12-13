package com.taskforce.superinvention.app.domain.region

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface RegionRepository : JpaRepository<Region, Long>, RegionRepositoryCustom {

}

interface RegionRepositoryCustom {
    fun findByLevelWithSubRegions(): List<Region>
}

@Repository
class RegionRepositoryImpl: RegionRepositoryCustom,
    QuerydslRepositorySupport(Region::class.java) {

    override fun findByLevelWithSubRegions(): List<Region> {
        val superRegion = QRegion.region
        val subRegion = QRegion("r2")

        val result = from(superRegion)
            .join(superRegion.subRegions, subRegion).fetchJoin()
            .where(superRegion.superRegion.isNull)

        val fetch = result.fetch().distinct()
        return fetch
    }
}