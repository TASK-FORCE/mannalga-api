package com.taskforce.superinvention.app.domain.club.album

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.taskforce.superinvention.app.domain.club.album.comment.QClubAlbumComment
import com.taskforce.superinvention.app.domain.club.album.like.QClubAlbumLike
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubAlbumRepository: JpaRepository<ClubAlbum, Long>, ClubAlbumRepositoryCustom

interface ClubAlbumRepositoryCustom {
    fun findClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption?, pageable: Pageable): QueryResults<Tuple>
}

@Repository
class ClubAlbumRepositoryImpl: ClubAlbumRepositoryCustom,
    QuerydslRepositorySupport(ClubAlbum::class.java) {

    override fun findClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption?, pageable: Pageable): QueryResults<Tuple> {
        val clubUser         = QClubUser.clubUser
        val clubAlbum        = QClubAlbum.clubAlbum
        val clubAlbumLike    = QClubAlbumLike.clubAlbumLike
        val clubAlbumComment = QClubAlbumComment.clubAlbumComment

        val query = from(clubAlbum)
                .select(
                        clubAlbum,
                        clubAlbumLike.count(),     // 좋아요  개수
                        clubAlbumComment.count()   // 댓글    개수
                )
                .leftJoin(clubAlbum.clubAlbumLikes, clubAlbumLike)
                .leftJoin(clubAlbum.clubAlbumComments, clubAlbumComment)
                .join(clubAlbum.writer, clubUser).fetchJoin()
                .groupBy(clubAlbum.seq)
                .where(clubAlbum.delete_flag.isFalse.and(clubAlbum.club.seq.eq(clubSeq)))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())

        if(searchOption!= null && searchOption.title.isNotBlank()) {
            query.where(clubAlbum.title.like("${searchOption.title}%"))
        }

        return query.fetchResults()
    }
}