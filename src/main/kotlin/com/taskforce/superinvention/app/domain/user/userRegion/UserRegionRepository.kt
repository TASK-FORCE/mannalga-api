package com.taskforce.superinvention.app.domain.user.userRegion

import com.taskforce.superinvention.app.domain.region.QRegion
import com.taskforce.superinvention.app.domain.user.QUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface UserRegionRepository : JpaRepository<UserRegion, Long>, UserRegionRepositoryCustom {
    fun findByUserSeq(userSeq: Long): List<UserRegion>
}

interface UserRegionRepositoryCustom {
    fun findByUserWithRegion(userSeq: Long): List<UserRegion>
}

@Repository
class UserRegionRepositoryImpl: UserRegionRepositoryCustom,
        QuerydslRepositorySupport(UserRegion::class.java){

    override fun findByUserWithRegion(userSeq: Long): List<UserRegion> {

        val user = QUser.user
        val userRegion = QUserRegion.userRegion
        val region = QRegion.region

        val query = from(userRegion)
                .join(userRegion.user, user).fetchJoin()
                .join(userRegion.region, region).fetchJoin()
                .where(userRegion.user.seq.eq(userSeq))
        return query.fetch()
    }
}
