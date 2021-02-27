package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.common.exception.common.ImplementationIsNotSupported
import org.springframework.stereotype.Service

@Service
class ImageResizeStrategyLocator(
    private val imageResizeStrategy: List<ImageResizeStrategy>
) {

    fun getStrategy(imageFormat: ImageFormat): ImageResizeStrategy {
        return imageResizeStrategy.find { service -> service.identify(imageFormat) }
            ?: throw ImplementationIsNotSupported()
    }

}