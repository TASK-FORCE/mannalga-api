package com.taskforce.superinvention.app.domain.club.album.comment

import com.blazebit.persistence.CTE
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@CTE
@Entity
class ClubAlbumCommentCTE(
        var content: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="parent_comment_seq")
        var parent: ClubAlbumComment?,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="club_user_seq")
        var clubUser: ClubUser,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="club_album_seq")
        var clubAlbum: ClubAlbum,

        var depth: Long,

        var subCommentCnt: Long?
): BaseEntity()