package com.developersyndicate.kxengine.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import com.developersyndicate.kxengine.platform.KXPlatform
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlesTexture(val resourcePath: String = "") : Texture {
    override var id: Int = 0
        private set
    override var width: Int = 0
        private set
    override var height: Int = 0
        private set

    private var lastModified: Long = 0L

    init {
        if (resourcePath.isNotEmpty()) {
            lastModified = KXPlatform.fileSystem.lastModified(resourcePath)
            loadTexture()
        }
    }

    constructor(width: Int, height: Int, color: Color = Color.WHITE) : this("") {
        this.width = width
        this.height = height
        
        val temp = IntArray(1)
        GLES30.glGenTextures(1, temp, 0)
        this.id = temp[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id)

        val buffer = ByteBuffer.allocateDirect(width * height * 4)
            .order(ByteOrder.nativeOrder())
        val r = (color.r * 255).toInt().toByte()
        val g = (color.g * 255).toInt().toByte()
        val b = (color.b * 255).toInt().toByte()
        val a = (color.a * 255).toInt().toByte()
        for (i in 0 until width * height) {
            buffer.put(r).put(g).put(b).put(a)
        }
        buffer.flip()

        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D,
            0,
            GLES30.GL_RGBA,
            width,
            height,
            0,
            GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE,
            buffer
        )

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
    }

    private fun loadTexture() {
        val imageBytes = KXPlatform.fileSystem.readBytes(resourcePath)
        
        val options = BitmapFactory.Options().apply {
            inPremultiplied = true
        }
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            ?: error("BitmapFactory failed to decode image: $resourcePath")

        width = bitmap.width
        height = bitmap.height

        if (id == 0) {
            val temp = IntArray(1)
            GLES30.glGenTextures(1, temp, 0)
            id = temp[0]
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        bitmap.recycle()
    }

    override fun checkAndReload(): Boolean {
        if (resourcePath.isEmpty()) return false
        val mod = KXPlatform.fileSystem.lastModified(resourcePath)
        if (mod > lastModified) {
            lastModified = mod
            loadTexture()
            println("AssetSystem: Texture reloaded: $resourcePath")
            return true
        }
        return false
    }

    override fun bind(unit: Int) {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + unit)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id)
    }

    override fun destroy() {
        if (id != 0) {
            GLES30.glDeleteTextures(1, intArrayOf(id), 0)
            id = 0
        }
    }
}
