package com.developersyndicate.kxengine.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.InputStream

class Texture(resourcePath: String) {

    val id: Int
    val width: Int
    val height: Int

    init {
        val imageBytes = readResource(resourcePath)

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

            id = glGenTextures()
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

    private fun readResource(path: String): ByteArray {
        val stream: InputStream =
            Texture::class.java.classLoader
                .getResourceAsStream(path)
                ?: error("Resource not found on classpath: $path")

        return stream.readBytes()
    }

    fun bind(unit: Int = 0) {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun destroy() {
        glDeleteTextures(id)
    }
}
