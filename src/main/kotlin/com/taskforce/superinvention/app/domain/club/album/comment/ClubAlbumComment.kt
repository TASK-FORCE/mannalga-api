package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.hibernate.annotations.Formula
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubAlbumComment(
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_user_seq")
    var clubUser: ClubUser,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_album_seq")
    var clubAlbum: ClubAlbum,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_comment_seq")
    var parent: ClubAlbumComment?,

    var depth: Long,

    var deleteFlag: Boolean ?= false
): BaseEntity() {

    // 직전 하위 뎁스의 댓글 개수만 보여줌
    @Formula("(select count(*) from club_album_comment cac where cac.parent_comment_seq = seq and cac.depth = depth+1)")
    var subCommentCnt: Long ?= null

    // 모든 하위 뎁스의 댓글 개수만 보여줌
    @Formula("(select count(*) from club_album_comment cac where cac.parent_comment_seq = seq and cac.depth > depth)")
    var totalSubCommentCnt: Long ?= null

    constructor(content: String, clubUser: ClubUser, clubAlbum: ClubAlbum) :this(
        content   = content,
        clubUser  = clubUser,
        clubAlbum = clubAlbum,
        parent    = null,
        depth     = 1
    )
}
