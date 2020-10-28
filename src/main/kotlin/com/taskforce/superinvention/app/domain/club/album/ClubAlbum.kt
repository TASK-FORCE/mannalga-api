package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity

@Entity
class ClubAlbum(
    val title      : String,
    val img_url    : String,
    val file_name  : String,
    val delete_flag: Boolean
): BaseEntity()