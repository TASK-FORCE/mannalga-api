package com.taskforce.superinvention.app.domain.club.board.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long>