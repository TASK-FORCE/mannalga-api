package com.taskforce.superinvention.app.web.dto.club

class ClubAddRequestDto(
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var mainImageUrl: String?
) {
}