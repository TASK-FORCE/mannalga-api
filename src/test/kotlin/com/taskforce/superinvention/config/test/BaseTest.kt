package com.taskforce.superinvention.config.test

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths

interface BaseTest {

    fun getResourcePath(): String {
        val resourceDirectory: Path = Paths.get("src", "test", "resources")
        return resourceDirectory.toFile().absolutePath
    }

    fun getResourceAsStream(path: String): InputStream {
        return this::class.java.getResourceAsStream(path)
    }

    fun getResourceByteArray(path: String): ByteArray {
        return IOUtils.toByteArray(this::class.java.getResourceAsStream(path))
    }
}