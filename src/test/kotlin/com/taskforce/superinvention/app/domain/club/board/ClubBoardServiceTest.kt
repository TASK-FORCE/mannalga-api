package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.ClubRepository
import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile


class ClubBoardServiceTest: IntegrationTest()  {

    @Autowired
    lateinit var clubBoardService: ClubBoardService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var clubRepository: ClubRepository

    @Autowired
    lateinit var fileService: FileService

    lateinit var registerBody: ClubBoardRegisterBody

    @BeforeEach
    fun setup() { }

    @Disabled
    @Test
    fun `게시판 글 등록 - 이미지 등록과 포함`() {

        // given
        val user = userRepository.findByUserId("1439528597")!!
        val clubSeq = 8866L

        // when
        val image = MockMultipartFile("files", "test.jpg", "image/jpg", getResourceAsStream("/img/test.jpg"))
        val s3path: S3Path = fileService.fileTempSave(image)
        registerBody = ClubBoardRegisterBody(
            title = "글 제목",
            content = "내용",
            imgList = listOf(s3path),
            category = ClubBoard.Category.NORMAL
        )


        val board = clubBoardService.registerClubBoard(user, clubSeq, registerBody)

        // then
        print(board)
    }


    @Disabled
    @Test
    fun `게시판 글 삭제`() {
        val user = userRepository.findByUserId("1439528597")!!
        val clubSeq = 88L

        val requestBody = registerBody

        // [1] 게시글 등록
        val clubBoard = clubBoardService.registerClubBoard(user, clubSeq, requestBody)

        // [2] 등록된 글 삭제
        clubBoardService.deleteClubBoard(user, clubBoardSeq = clubBoard.seq!!)
    }

    @Disabled
    @Test
    fun `작성자 또는 매니저가 아니면 게시글을 지우지 못함`() {

        // given
        val writer= userRepository.findByUserId("1439528597")!!
        val nonWriter= userRepository.findByUserId("1451001649")!!
        val club  = clubRepository.findBySeq(88L)

        val requestBody = registerBody

        // when

        // [1] 게시글 등록
        val clubBoard4writer  = clubBoardService.registerClubBoard(writer, club.seq!!, requestBody)

        // then
        Assertions.assertThrows(InsufficientAuthException::class.java) {

            // [2] 등록된 글 삭제
            clubBoardService.deleteClubBoard(nonWriter , clubBoardSeq = clubBoard4writer.seq!!)
        }
    }
}