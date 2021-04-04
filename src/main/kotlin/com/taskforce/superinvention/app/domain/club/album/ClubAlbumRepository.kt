package com.taskforce.superinvention.app.domain.club.album

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubAlbumRepository: JpaRepository<ClubAlbum, Long>, ClubAlbumRepositoryCustom

interface ClubAlbumRepositoryCustom {
    fun findClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption, pageable: Pageable): Page<ClubAlbum>
}

@Repository
class ClubAlbumRepositoryImpl: ClubAlbumRepositoryCustom,
    QuerydslRepositorySupport(ClubAlbum::class.java) {

    override fun findClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption, pageable: Pageable): Page<ClubAlbum> {
        val clubAlbum = QClubAlbum.clubAlbum
        val club      = QClub.club
        val clubUser  = QClubUser.clubUser
        val user      = QUser.user

        val query =
                from(clubAlbum)
                .join(clubAlbum.club, club).fetchJoin()
                .leftJoin(clubAlbum.writer , clubUser).fetchJoin()
                .leftJoin(clubUser.user, user).fetchJoin()
                .where(clubAlbum.delete_flag.isFalse,
                       eqSeq(clubAlbum.club.seq, clubSeq),
                       likeIfNotBlank(clubAlbum.title, searchOption.title)
                )

        if (pageable != Pageable.unpaged()) {
            query.offset(pageable.offset)
                 .limit(pageable.pageSize.toLong())
        }

        query.orderBy(clubAlbum.createdAt.desc())
        val fetchResults = query.fetchResults()

        return PageImpl(fetchResults.results, pageable, fetchResults.total)
    }

    private fun likeIfNotBlank(title: StringPath, keyword: String): BooleanExpression? {

        if(keyword.isNotBlank()) {
            return title.like("%${keyword}%")
        }

        return null
    }

    private fun eqSeq(seq: NumberPath<Long>, clubSeq: Long): Predicate {
        return seq.eq(clubSeq)
    }
}
