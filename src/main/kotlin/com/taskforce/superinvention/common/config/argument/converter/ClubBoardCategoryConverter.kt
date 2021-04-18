package com.taskforce.superinvention.common.config.argument.converter

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.common.exception.InvalidInputException
import org.springframework.core.convert.converter.Converter

class ClubBoardCategoryConverter: Converter<String, ClubBoard.Category> {

    override fun convert(source: String): ClubBoard.Category {
        return ClubBoard.Category.fromCategoryLabel(source)
            ?: throw InvalidInputException(message = "해당 카테고리로를 검색할 수 없습니다.")
    }
}