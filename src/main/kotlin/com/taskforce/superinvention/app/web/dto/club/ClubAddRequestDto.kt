package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

class ClubAddRequestDto(
    @get:NotBlank(message = "모임 명을 입력해주세요")
    var name: String,
    @get:NotBlank(message = "모임 설명을 작성해주세요")
    var description: String,

    @get:Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다")
    var maximumNumber: Long,

    var img: S3Path?,

    @get:NotEmpty(message = "모임 관심사를 하나 이상 선택해주세요")
    var interestList: List<InterestRequestDto>,

    @get:NotEmpty(message = "모임 지역을 하나 이상 선택해주세요")
    var regionList: List<RegionRequestDto>
)
