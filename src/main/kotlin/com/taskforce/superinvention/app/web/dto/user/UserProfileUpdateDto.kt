package com.taskforce.superinvention.app.web.dto.user

import com.taskforce.superinvention.common.util.aws.s3.S3Path

data class UserProfileUpdateDto (
    val profileImage: S3Path
)