package com.taskforce.superinvention.app.domain.club

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.region.ClubRegion
import org.hibernate.annotations.Formula
import javax.persistence.*

@Entity
@JsonIdentityInfo(property = "objId", generator = ObjectIdGenerators.StringIdGenerator::class)
class Club(
    var name           : String,
    var description    : String,
    var maximumNumber  : Long,
    var mainImageUrl   : String?,  // 절대경로 (도메인 포함)
    var mainImagePath  : String?,  // 경로
    var mainImageName  : String?,  // 파일명
): BaseEntity() {

    // test - only
    constructor(name          : String,
                description   : String,
                maximumNumber : Long,
                mainImageUrl  : String?): this(
        name          = name,
        description   = description,
        maximumNumber = maximumNumber,
        mainImageUrl  = mainImageUrl,
        mainImagePath = "",
        mainImageName = ""
    )

    @Formula("(SELECT count(*)\n" +
            "FROM club_user cu\n" +
            "JOIN club_user_role cur on cu.seq = cur.club_user_seq\n" +
            "JOIN role r on cur.role_seq = r.seq\n" +
            "WHERE r.name IN ('CLUB_MEMBER', 'MANAGER', 'MASTER') AND cu.club_seq = seq)")
    var userCount: Long ?= null

    @OneToMany
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubInterests: Set<ClubInterest>

    @OneToMany
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubRegions: Set<ClubRegion>

    @OneToMany
    @JoinColumn(name = "club_seq")
    lateinit var clubUser: Set<ClubUser>

}
