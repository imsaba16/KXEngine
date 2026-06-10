package com.developersyndicate.kxengine.platform

import android.content.Context
import java.io.InputStream

class AndroidFileSystem(private val context: Context) : IFileSystem {
    override fun readBytes(path: String): ByteArray {
        val assetPath = cleanPath(path)
        return context.assets.open(assetPath).use { it.readBytes() }
    }

    override fun readText(path: String): String {
        return String(readBytes(path))
    }

    override fun exists(path: String): Boolean {
        val assetPath = cleanPath(path)
        var stream: InputStream? = null
        return try {
            stream = context.assets.open(assetPath)
            true
        } catch (e: Exception) {
            false
        } finally {
            stream?.close()
        }
    }

    override fun lastModified(path: String): Long {
        // Assets are packaged in the APK, so we can treat their modification time as 0.
        return 0L
    }

    private fun cleanPath(path: String): String {
        return if (path.startsWith("assets/")) {
            path.substring("assets/".length)
        } else {
            path
        }
    }
}
