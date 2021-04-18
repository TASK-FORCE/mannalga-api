package com.taskforce.superinvention.app.domain.common.image.webp.convert.handler

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 애니메이션 GIF -> WEBP는 따로 래핑된 라이브러리가 없어 직접 만듬
 */
class Gif2WebpHandler : WebpHandler() {
    companion object {

        var binary: Path

        private fun installGif2Webp() {
            installBinary(binary, "/webp_binaries/gif2webp", "/dist_webp_binaries/gif2webp")
        }

        init {
            try {
                // write out binary to a location we can execute it from
                binary = createPlaceholder("gif2webp")
                installGif2Webp()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun convert(gifFile: File, q: Int, m: Int, lossy: Boolean, mixed: Boolean, multiThreaded: Boolean): ByteArray {
        return try {
            val target = Files.createTempFile("to_webp", "webp").toAbsolutePath()
            convert(gifFile.toPath().toAbsolutePath(), target, m, q, lossy, mixed, multiThreaded)
            Files.readAllBytes(target)
        } finally {
            gifFile.delete()
        }
    }

    private fun convert(
        input: Path,
        target: Path,
        m: Int,
        q: Int,
        lossy: Boolean,
        mixed: Boolean,
        multiThreaded: Boolean
    ) {
        val stdout = Files.createTempFile("stdout", "webp")
        val commands: MutableList<String> = ArrayList(5)

        commands.add(binary.toAbsolutePath().toString())

        if (m >= 0) {
            commands.add("-m")
            commands.add(m.toString())
        }
        if (q >= 0) {
            commands.add("-q")
            commands.add(q.toString())
        }

        if (lossy) {
            commands.add("-lossy")
        }

        if(mixed) {
            commands.add("-mixed")
        }

        if(multiThreaded) {
            commands.add("-mt")
        }

        commands.add(input.toAbsolutePath().toString())
        commands.add("-o")
        commands.add(target.toAbsolutePath().toString())

        val builder = ProcessBuilder(commands)
        builder.redirectErrorStream(true)
        builder.redirectOutput(stdout.toFile())
        val process = builder.start()

        try {
            process.waitFor(5, TimeUnit.MINUTES)
        } catch (e: InterruptedException) {
            throw IOException(e)
        }

        val exitStatus = process.exitValue()
        if (exitStatus != 0) {
            val error = Files.readAllLines(stdout)
            throw IOException(error.toString())
        }
    }
}
