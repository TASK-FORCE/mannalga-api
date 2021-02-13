package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentRepository
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeRepository
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentRepository
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImgRepository
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.ClubInterestRepository
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import com.taskforce.superinvention.app.domain.meeting.MeetingApplicationRepository
import com.taskforce.superinvention.app.domain.meeting.MeetingRepository
import com.taskforce.superinvention.app.domain.region.ClubRegion
import com.taskforce.superinvention.app.domain.region.ClubRegionRepository
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.region.RegionService
import com.taskforce.superinvention.app.domain.role.ClubUserRoleRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

internal class ClubServiceTest {

    lateinit var clubService: ClubService
    lateinit var clubUserRepository: ClubUserRepository
    lateinit var clubInterestRepository: ClubInterestRepository
    lateinit var clubRegionRepository: ClubRegionRepository
    lateinit var clubRepository: ClubRepository
    lateinit var clubUserRoleRepository: ClubUserRoleRepository
    lateinit var clubUserService: ClubUserService
    lateinit var interestService: InterestService
    lateinit var regionService: RegionService
    lateinit var roleService: RoleService
    lateinit var userRepository: UserRepository
    lateinit var clubAlbumRepository: ClubAlbumRepository
    lateinit var clubAlbumCommentRepository: ClubAlbumCommentRepository
    lateinit var clubBoardRepository: ClubBoardRepository
    lateinit var clubBoardCommentRepository: ClubBoardCommentRepository
    lateinit var clubBoardLikeRepository: ClubBoardLikeRepository
    lateinit var clubBoardImgRepository: ClubBoardImgRepository
    lateinit var meetingRepository: MeetingRepository
    lateinit var meetingApplicationRepository: MeetingApplicationRepository
    lateinit var clubAlbumLikeRepository: ClubAlbumLikeRepository

    @BeforeEach
    fun init() {
        clubUserRepository = mockk()
        clubInterestRepository = mockk()
        clubRegionRepository = mockk()
        clubRepository = mockk()
        clubUserRoleRepository = mockk()
        clubUserService = mockk()
        interestService = mockk()
        regionService = mockk()
        roleService = mockk()
        userRepository = mockk()
        clubAlbumRepository = mockk()
        clubAlbumCommentRepository = mockk()
        clubBoardRepository = mockk()
        clubBoardCommentRepository = mockk()
        clubBoardLikeRepository = mockk()
        clubBoardImgRepository = mockk()
        meetingRepository = mockk()
        meetingApplicationRepository = mockk()
        clubAlbumLikeRepository = mockk()

        clubService = ClubService(
            clubUserRepository = clubUserRepository,
            clubInterestRepository = clubInterestRepository,
            clubRegionRepository = clubRegionRepository,
            clubRepository = clubRepository,
            clubUserRoleRepository = clubUserRoleRepository,
            clubUserService = clubUserService,
            interestService = interestService,
            regionService = regionService,
            roleService = roleService,
            userRepository = userRepository,
            clubAlbumRepository = clubAlbumRepository,
            clubAlbumCommentRepository = clubAlbumCommentRepository,
            clubBoardRepository = clubBoardRepository,
            clubBoardCommentRepository = clubBoardCommentRepository,
            clubBoardLikeRepository = clubBoardLikeRepository,
            clubBoardImgRepository = clubBoardImgRepository,
            meetingRepository = meetingRepository,
            meetingApplicationRepository = meetingApplicationRepository,
            clubAlbumLikeRepository = clubAlbumLikeRepository
        )

        every { clubRepository.save(any()) }.returns(mockk())
        every { clubUserRepository.save(any()) }.returns(mockk())
        every { clubInterestRepository.saveAll(any<Iterable<ClubInterest>>()) }.returns(mockk())
        every { clubRegionRepository.saveAll(any<Iterable<ClubRegion>>()) }.returns(mockk())
        every { roleService.findByRoleName(any()) }.returns(mockk())
        every { clubUserRoleRepository.save(any()) }.returns(mockk())
        every { clubUserRepository.findByClubAndUser(any(), any()) }.returns(mockk())
        every { roleService.hasClubManagerAuth(any()) }.returns(true)
        every { clubRegionRepository.findByClub(any()) }.returns(mockk())
        every { clubRegionRepository.deleteAll(any()) }.returns(mockk())
        every { clubInterestRepository.findByClub(any()) }.returns(mockk())
        every { clubInterestRepository.deleteAll(any()) }.returns(mockk())
    }


    @Test
    fun `모임 생성시 관심사 및 지역 제한 해제`() {
        // given
        val superUser = mockk<User>()
        val clubInterestDtoList = listOf(
            InterestRequestDto(1, 1), InterestRequestDto(2, 2), InterestRequestDto(3, 3)
        )
        val clubRegionDtoList = listOf(
            RegionRequestDto(1, 1), RegionRequestDto(2, 2), RegionRequestDto(3, 3)
        )
        val club = Club(
            name = "mock club",
            description = "mock desc",
            maximumNumber = 10,
            mainImageUrl = "asdasd.png"
        ).apply { seq = 1 }

        val interestGroup1 = InterestGroup("1번 그룹").apply { seq = 1 }
        val interestGroup2 = InterestGroup("2번 그룹").apply { seq = 2 }

        val interest1 = Interest("1번 그룹 관심사 1", interestGroup1).apply { seq = 1 }
        val interest2 = Interest("1번 그룹 관심사 2", interestGroup1).apply { seq = 2 }
        val interest3 = Interest("2번 그룹 관심사 1", interestGroup2).apply { seq = 3 }

        val superRegion1 = Region(null, "루트 1", "루트 1", 1)
        val superRegion2 = Region(null, "루트 2", "루트 2", 1)

        val subRegion1 = Region(superRegion1, "서브 1 - 루트 1", "루트 1/서브 1 - 루트 1", 2)
        val subRegion2 = Region(superRegion1, "서브 2 - 루트 1", "루트 1/서브 1 - 루트 1", 2)
        val subRegion3 = Region(superRegion2, "서브 1 - 루트 2", "루트 2/서브 1 - 루트 2", 2)


        every { interestService.findBySeq(1) }.returns(interest1)
        every { interestService.findBySeq(2) }.returns(interest2)
        every { interestService.findBySeq(3) }.returns(interest3)
        every { regionService.findBySeq(1) }.returns(subRegion1)
        every { regionService.findBySeq(2) }.returns(subRegion2)
        every { regionService.findBySeq(3) }.returns(subRegion3)


        // when
        clubService.addClub(club, superUser, clubInterestDtoList, clubRegionDtoList)

        // then
        // success
    }

    @Test
    fun `모임 관심사 변경 제한 해제`() {
        // given
        val user = mockk<User>()
        val clubInterestDtoSet = setOf(
            InterestRequestDto(1, 1), InterestRequestDto(2, 2), InterestRequestDto(3, 3)
        )

        val club = Club(
            name = "mock club",
            description = "mock desc",
            maximumNumber = 10,
            mainImageUrl = "asdasd.png"
        ).apply { seq = 1 }

        val interestGroup1 = InterestGroup("1번 그룹").apply { seq = 1 }
        val interestGroup2 = InterestGroup("2번 그룹").apply { seq = 2 }

        val interest1 = Interest("1번 그룹 관심사 1", interestGroup1).apply { seq = 1 }
        val interest2 = Interest("1번 그룹 관심사 2", interestGroup1).apply { seq = 2 }
        val interest3 = Interest("2번 그룹 관심사 1", interestGroup2).apply { seq = 3 }


        every { clubRepository.findByIdOrNull(1) }.returns(club)

        every { interestService.findBySeq(1) }.returns(interest1)
        every { interestService.findBySeq(2) }.returns(interest2)
        every { interestService.findBySeq(3) }.returns(interest3)

        // when
        clubService.changeClubInterests(user, club.seq!!, clubInterestDtoSet)

        // then
        // success
    }

    @Test
    fun `모임 지역 변경 제한 해제`() {
        // given
        val user = mockk<User>()
        val clubRegionDtoSet = setOf(
            RegionRequestDto(1, 1), RegionRequestDto(2, 2), RegionRequestDto(3, 3)
        )
        val club = Club(
            name = "mock club",
            description = "mock desc",
            maximumNumber = 10,
            mainImageUrl = "asdasd.png"
        ).apply { seq = 1 }

        val superRegion1 = Region(null, "루트 1", "루트 1", 1)
        val superRegion2 = Region(null, "루트 2", "루트 2", 1)

        val subRegion1 = Region(superRegion1, "서브 1 - 루트 1", "루트 1/서브 1 - 루트 1", 2)
        val subRegion2 = Region(superRegion1, "서브 2 - 루트 1", "루트 1/서브 1 - 루트 1", 2)
        val subRegion3 = Region(superRegion2, "서브 1 - 루트 2", "루트 2/서브 1 - 루트 2", 2)

        every { clubRepository.findByIdOrNull(1) }.returns(club)

        every { regionService.findBySeq(1) }.returns(subRegion1)
        every { regionService.findBySeq(2) }.returns(subRegion2)
        every { regionService.findBySeq(3) }.returns(subRegion3)


        // when
        clubService.changeClubRegions(user, club.seq!!, clubRegionDtoSet)

        // then
        // success
    }
}