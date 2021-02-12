package com.taskforce.superinvention.app.domain.common.image.webp.convert.handler

import org.apache.commons.lang3.SystemUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.TimeUnit

abstract class WebpHandler {

    companion object {
        fun createPlaceholder(name: String): Path {
            return Files.createTempFile(name, "binary")
        }

        fun installBinary(output: Path, vararg sources: String) {
            for (source in sources) {
                val inputStream = WebpHandler::class.java.getResourceAsStream(source)
                if (inputStream != null) {
                    Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING)
                    inputStream.close()
                    if (!SystemUtils.IS_OS_WINDOWS) {
                        setExecutable(output)
                    }
                    return
                }
            }
            throw IOException("Could not locate webp binary at " + Arrays.toString(sources))
        }

        private fun setExecutable(output: Path): Boolean {
            return try {
                ProcessBuilder("chmod", "+x", output.toAbsolutePath().toString())
                    .start()
                    .waitFor(30, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                throw IOException(e)
            }
        }
    }
}
