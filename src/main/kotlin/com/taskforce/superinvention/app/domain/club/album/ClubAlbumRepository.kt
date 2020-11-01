package com.taskforce.superinvention.app.domain.club.album

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClubAlbumRepository: JpaRepository<ClubAlbum, Long> {

}

interface ClubAlbumRepositoryCustom {

}

@Repository
class ClubAlbumRepositoryImpl: ClubAlbumRepositoryCustom {

}