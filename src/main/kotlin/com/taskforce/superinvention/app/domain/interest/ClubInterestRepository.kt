package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.interest.interest.QInterest
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubInterestRepository: JpaRepository<ClubInterest, Long>, ClubInterestRepositoryCustom {
    fun findByClub(club: Club): List<ClubInterest>
}

interface ClubInterestRepositoryCustom {
    fun findWithInterestGroup(clubSeq: Long) : List<ClubInterest>
}

@Repository
class ClubInterestRepositoryImpl: ClubInterestRepositoryCustom,
    QuerydslRepositorySupport(ClubInterest::class.java){

    override fun findWithInterestGroup(clubSeq: Long): List<ClubInterest> {
        val club = QClub.club
        val clubInterest = QClubInterest.clubInterest
        val interestGroup = QInterestGroup.interestGroup

        val query = from(clubInterest)
                .join(clubInterest.club, club)
                .join(clubInterest.interest.interestGroup, interestGroup)
                .where(clubInterest.club.seq.eq(clubSeq))

        return query.fetch()
    }
}
