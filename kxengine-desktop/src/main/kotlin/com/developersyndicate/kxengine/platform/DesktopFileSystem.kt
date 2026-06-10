package com.developersyndicate.kxengine.platform

import java.io.File
import java.io.InputStream

class DesktopFileSystem : IFileSystem {
    override fun readBytes(path: String): ByteArray {
        val file = File("src/main/resources/$path")
        if (file.exists()) {
            return file.readBytes()
        }
        val stream: InputStream = DesktopFileSystem::class.java.classLoader.getResourceAsStream(path)
            ?: error("File not found on classpath: $path")
        return stream.use { it.readBytes() }
    }

    override fun readText(path: String): String {
        return String(readBytes(path))
    }

    override fun exists(path: String): Boolean {
        val file = File("src/main/resources/$path")
        if (file.exists()) return true
        val stream = DesktopFileSystem::class.java.classLoader.getResourceAsStream(path)
        if (stream != null) {
            stream.close()
            return true
        }
        return false
    }

    override fun lastModified(path: String): Long {
        val file = File("src/main/resources/$path")
        if (file.exists()) {
            return file.lastModified()
        }
        return 0L
    }
}
