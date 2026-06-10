package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.platform.KXPlatform
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class LwjglTexture(val resourcePath: String = "") : Texture {

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
        this.id = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, id)

        val buffer = MemoryUtil.memAlloc(width * height * 4)
        val r = (color.r * 255).toInt().toByte()
        val g = (color.g * 255).toInt().toByte()
        val b = (color.b * 255).toInt().toByte()
        val a = (color.a * 255).toInt().toByte()
        for (i in 0 until width * height) {
            buffer.put(r).put(g).put(b).put(a)
        }
        buffer.flip()

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            buffer
        )

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        MemoryUtil.memFree(buffer)
    }

    private fun loadTexture() {
        val imageBytes = KXPlatform.fileSystem.readBytes(resourcePath)

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val comp = stack.mallocInt(1)

            stbi_set_flip_vertically_on_load(true)

            val imageBuffer = MemoryUtil.memAlloc(imageBytes.size)
            imageBuffer.put(imageBytes).flip()

            val image = stbi_load_from_memory(
                imageBuffer,
                w,
                h,
                comp,
                4
            ) ?: error("STB failed to load image: $resourcePath")

            width = w[0]
            height = h[0]

            if (id == 0) {
                id = glGenTextures()
            }
            glBindTexture(GL_TEXTURE_2D, id)

            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                width,
                height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image
            )

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

            stbi_image_free(image)
            MemoryUtil.memFree(imageBuffer)
        }
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
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    override fun destroy() {
        if (id != 0) {
            glDeleteTextures(id)
            id = 0
        }
    }
}
