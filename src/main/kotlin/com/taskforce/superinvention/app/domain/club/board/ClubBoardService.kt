package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import org.springframework.stereotype.Service

@Service
class ClubBoardService(
        private val roleService: RoleService,
        private val clubBoardRepository: ClubBoardRepository,
        private val clubUserRepository: ClubUserRepository
) {

    fun getClubBoardList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoardDto> {

        val pageRequest: Pageable = PageRequest.of(pageable.pageNumber - 1, pageable.pageSize)
        val search = clubBoardRepository.search(pageRequest, searchOpt, clubSeq)

        return search.map { cb -> ClubBoardDto(cb)}
    }

    /**
     * 클럽 게시판 글 등록
     */
    @Transactional(rollbackFor = [Exception::class])
    fun registerClubBoard(user: User, clubSeq: Long, body: ClubBoardDto) {
        val writer: ClubUser = clubUserRepository.findByClub_SeqAndUser(clubSeq, user)

        val clubBoard = ClubBoard (
                title   = body.title,
                content = body.content,
                clubUser = writer,
                topFixedFlag = false,
                deleteFlag   = false,
                notificationFlag = false,
                club = writer.club
        )

        clubBoardRepository.save(clubBoard)
    }

    @Transactional
    fun removeClubBoard(user: User, clubBoardSeq: Long) {
        val clubBoard: ClubBoard = clubBoardRepository.findBySeq(clubBoardSeq)
        val clubUser = clubBoard.clubUser

        if(roleService.hasClubManagerAuth(clubUser)) {
            throw InsufficientAuthenticationException("충분한 권한이 없습니다.")
        }

        clubBoard.deleteFlag = false;
        clubBoardRepository.save(clubBoard)
    }
}