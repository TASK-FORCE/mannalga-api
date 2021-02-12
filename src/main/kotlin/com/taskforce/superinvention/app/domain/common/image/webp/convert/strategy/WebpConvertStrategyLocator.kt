package com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.common.exception.common.ImplementationIsNotSupported
import org.springframework.stereotype.Service

@Service
class WebpConvertStrategyLocator(
    private val webpConvertStrategy: List<WebpConvertStrategy>
) {

    fun getStrategy(imageFormat: ImageFormat): WebpConvertStrategy {
        return webpConvertStrategy.find { service -> service.identify(imageFormat) }
            ?: throw ImplementationIsNotSupported()
    }
}