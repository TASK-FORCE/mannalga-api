package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
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
        var parentComment: ClubAlbumComment?

): BaseEntity()