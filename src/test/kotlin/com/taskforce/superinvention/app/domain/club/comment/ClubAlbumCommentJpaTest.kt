package com.taskforce.superinvention.app.domain.club.comment

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubRepository
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.config.test.DataJpaRepoTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ClubAlbumCommentJpaTest: DataJpaRepoTest() {

    @Autowired
    lateinit var sut: ClubAlbumCommentRepository

    @Autowired
    lateinit var userRepo    : UserRepository

    @Autowired
    lateinit var clubRepo    : ClubRepository

    @Autowired
    lateinit var clubUserRepo : ClubUserRepository

    @Autowired
    lateinit var clubAlbumRepo: ClubAlbumRepository

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubAlbum: ClubAlbum

    @BeforeEach
    fun setup() {

        club = clubRepo.save(Club(
            name = "테스트 모임",
            description   = "",
            maximumNumber = 10,
            mainImageUrl  = ""
        ))

        user     = userRepo.save(User("12345", "sight"))
        clubUser = clubUserRepo.save(ClubUser(club, user, isLiked = true))
        clubAlbum = clubAlbumRepo.save(ClubAlbum (
            club        = club,
            writer      = clubUser,
            title       = "모임 사진첩 사진 1",
            img_url     = "이미지 URL",
            file_name   = "파일 이름",
            delete_flag = false
        ))
    }

    @Test
    fun `JPA - 모임 사진첩 계층형 댓글 테스트`() {
        // scenario : 1개의 루트 댓글, 2개의 뎁스, 각 뎁스당 댓글 3개씩,
        // 1 + 1*(3 + 3*3) = 13

        val rootCommentCnt  = 1
        val depthCommentCnt = 3
        val maxDepth        = 3
        val comments = arrayListOf<ClubAlbumComment>()

        val rootComment = ClubAlbumComment(
            content = "모임 댓글 - 뎁스 1",
            clubUser  = clubUser,
            clubAlbum = clubAlbum
        )
        comments.add(sut.save(rootComment))

        for (i in 1..rootCommentCnt) {
            recursiveAddComment(
                curDepth = 2,
                maxDepth = maxDepth,
                commentCntPerDepth = depthCommentCnt,
                parentComment = rootComment,
                comments = comments
            )
        }

        val childComments = sut.findChildCommentsWithWriter(rootComment.seq!!, rootComment.depth + 1, maxDepth.toLong())

        Assertions.assertEquals(13, comments.size)
        Assertions.assertEquals(12, childComments.size)

        // depth 별 검증
        Assertions.assertEquals(3, childComments.filter { comment -> comment.depth == 2L}.size)
        Assertions.assertEquals(9, childComments.filter { comment -> comment.depth == 3L}.size)
    }

    private fun recursiveAddComment(
        curDepth: Int,
        maxDepth: Int,
        commentCntPerDepth: Int,
        parentComment: ClubAlbumComment,
        comments: MutableList<ClubAlbumComment>
    ) {

        if(curDepth == maxDepth + 1) { return }

        for (i in 1..commentCntPerDepth) {
            val comment = ClubAlbumComment(
                content = "모임 댓글 - 뎁스 $curDepth - $i",
                clubUser  = clubUser,
                clubAlbum = clubAlbum,
                parent = parentComment,
                depth  = curDepth.toLong()
            )
            sut.save(comment)
            comments.add(comment)
            recursiveAddComment(
                curDepth = curDepth + 1,
                maxDepth = maxDepth,
                commentCntPerDepth = commentCntPerDepth,
                parentComment      = comment,
                comments           = comments
            )
        }
    }
}