package com.taskforce.superinvention.app.domain.common.image.webp.convert.handler

import java.io.File

class WebpAnimatedWriter(
    private var q: Int = -1,
    private var m: Int = -1,
    private var lossy: Boolean = false,
) {

    companion object {
        val DEFAULT: WebpAnimatedWriter = WebpAnimatedWriter()
        val LOSSY_COMPRESSION: WebpAnimatedWriter = DEFAULT.withLossy()
    }

    private val handler = Gif2WebpHandler()

    fun withQ(q: Int): WebpAnimatedWriter {
        require(q >= 0) { "q must be between 0 and 100" }
        require(q <= 100) { "q must be between 0 and 100" }
        return WebpAnimatedWriter(q, m, lossy)
    }

    fun withM(m: Int): WebpAnimatedWriter {
        require(m >= 0) { "m must be between 0 and 6" }
        require(m <= 6) { "m must be between 0 and 6" }
        return WebpAnimatedWriter(q, m, lossy)
    }

    fun withLossy(): WebpAnimatedWriter {
        return WebpAnimatedWriter(q, m, true)
    }

    fun writeAsByteArray(gifImage: File): ByteArray {
        return handler.convert(gifImage, q, m, lossy)
    }
}