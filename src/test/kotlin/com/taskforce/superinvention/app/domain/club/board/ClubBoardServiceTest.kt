package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.ClubRepository
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@Disabled
class ClubBoardServiceTest: IntegrationTest()  {

    @Autowired
    lateinit var sut: ClubBoardService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var clubRepository: ClubRepository

    @Test
    fun `게시판 리스트 조회`() {

        // given
        val pageable = PageRequest.of(1, 10)
        val searchOpt = ClubBoardSearchOpt()
        val clubSeq =  88L

        // when
        val searchInList = sut.getClubBoardList(pageable, searchOpt, clubSeq)

        searchInList.content

        print(searchInList)
    }

    @Test
    fun `게시판 글 등록`() {
        val user = userRepository.findByUserId("1439528597")!!
        val clubSeq = 88L

        val s3path = S3Path(
                absolutePath = "https://super-invention-static.s3.ap-northeast-2.amazonaws.com/test/i3.jpg",
                fileName = "i3.jpg",
                filePath = "test/i3.jpg"
        )

        val dummyImgList = listOf(
                s3path, // title
                s3path,
                s3path
        )

        val requestBody = ClubBoardBody(
                title   = "글 제목",
                content = "내용",
                isTopFixed   = false,
                isNotifiable = false,
                imgList = dummyImgList
        )

        sut.registerClubBoard(user, clubSeq, requestBody);
    }

    @Test
    fun `게시판 글 삭제`() {
        val user = userRepository.findByUserId("1439528597")!!
        val clubSeq = 88L

        val s3path = S3Path(
                absolutePath = "https://super-invention-static.s3.ap-northeast-2.amazonaws.com/test/i3.jpg",
                fileName = "i3.jpg",
                filePath = "test/i3.jpg"
        )

        val dummyImgList = listOf(
                s3path, // title
                s3path,
                s3path
        )

        val requestBody = ClubBoardBody(
                title   = "글 제목",
                content = "내용",
                isTopFixed   = false,
                isNotifiable = false,
                imgList = dummyImgList
        )

        // [1] 게시글 등록
        val clubBoard = sut.registerClubBoard(user, clubSeq, requestBody);

        // [2] 등록된 글 삭제
        sut.deleteClubBoard(user, clubBoardSeq = clubBoard.seq!!);
    }

    @Test
    fun `작성자 또는 매니저가 아니면 게시글을 지우지 못함`() {

        // given
        val writer= userRepository.findByUserId("1439528597")!!
        val nonWriter= userRepository.findByUserId("1451001649")!!
        val club  = clubRepository.findBySeq(88L)

        val s3path = S3Path(
                absolutePath = "https://super-invention-static.s3.ap-northeast-2.amazonaws.com/test/i3.jpg",
                fileName = "i3.jpg",
                filePath = "test/i3.jpg"
        )

        val dummyImgList = listOf(
                s3path, // title
                s3path,
                s3path
        )

        val requestBody = ClubBoardBody(
                title   = "글 제목",
                content = "내용",
                isTopFixed   = false,
                isNotifiable = false,
                imgList = dummyImgList
        )

        // when

        // [1] 게시글 등록
        val clubBoard4writer  = sut.registerClubBoard(writer, club.seq!!, requestBody)

        // then
        Assertions.assertThrows(InsufficientAuthException::class.java) {

            // [2] 등록된 글 삭제
            sut.deleteClubBoard(nonWriter , clubBoardSeq = clubBoard4writer.seq!!)
        }
    }
}