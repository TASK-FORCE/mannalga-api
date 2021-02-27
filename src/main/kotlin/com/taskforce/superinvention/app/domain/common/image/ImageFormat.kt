package com.taskforce.superinvention.app.domain.common.image

enum class ImageFormat {
    JPEG, JPG, PNG, GIF, WEBP;

    companion object {
        private val map = values()
        fun extensionOf(value: String): ImageFormat? = values().find { it.name == value.toUpperCase() }
    }
}