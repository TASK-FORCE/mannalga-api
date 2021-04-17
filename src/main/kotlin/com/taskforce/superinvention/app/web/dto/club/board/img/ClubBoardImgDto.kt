package com.taskforce.superinvention.app.web.dto.club.board.img

import com.taskforce.superinvention.common.util.aws.s3.S3Path

data class ClubBoardImgEditS3Path (
    val imgSeq: Long?,
    val img: S3Path
)
