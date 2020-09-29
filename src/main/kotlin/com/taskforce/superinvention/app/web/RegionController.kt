package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.region.RegionService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.region.RegionDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RegionController(
        val regionService: RegionService
) {

    @GetMapping("/regions")
    fun getAllRegionList(): ResponseDto<List<RegionDto>> {
        val findByLevel = regionService.findAllRegionDtoList()
        return ResponseDto(data = findByLevel)
    }
}