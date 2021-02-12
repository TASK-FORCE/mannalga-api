package com.taskforce.superinvention.app.domain.common.image

import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpConvertService
import com.taskforce.superinvention.common.exception.InvalidInputException
import org.springframework.stereotype.Service

@Service
class ImageService(
    private val webpConversionService: WebpConvertService,
) {

    companion object {
        val invalidInputException = InvalidInputException()
    }
}