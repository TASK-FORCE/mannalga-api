package com.taskforce.superinvention.app.web.dto.common

import org.springframework.data.domain.Page

data class PageDto<T>(
    val pageable  : PageableDto<T>,
    val content   : List<T>,
    val last      : Boolean,
    val size      : Int,
    val totalPages: Int
) {
    constructor(page : Page<T>) : this(
        content    = page.content,
        pageable   = PageableDto(page),
        last       = page.isLast,
        size       = page.size,
        totalPages = page.totalPages
    )
}

data class PageableDto <T> (
    val pageNumber : Int
) {
    constructor(page: Page<T>): this (
        pageNumber = page.pageable.pageNumber
    )
}