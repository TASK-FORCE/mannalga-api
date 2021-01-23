package com.taskforce.superinvention.app.domain.club.album

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.NumberPath
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

        val query = from(clubAlbum)
                .where(clubAlbum.delete_flag.isFalse, eqSeq(clubAlbum.club.seq, clubSeq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())

        if(searchOption.title.isNotBlank()) {
            query.where(clubAlbum.title.like("${searchOption.title}%"))
        }

        val fetchResults = query.fetchResults()

        return PageImpl(fetchResults.results, pageable, fetchResults.total)
    }

    private fun eqSeq(seq: NumberPath<Long>, clubSeq: Long): Predicate {
        return seq.eq(clubSeq)
    }
}