package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity

@Entity
class Club(
    var name: String,
    var description: String,
    var maximumNumber: Long,
    var mainImageUrl: String?
) : BaseEntity() {

}