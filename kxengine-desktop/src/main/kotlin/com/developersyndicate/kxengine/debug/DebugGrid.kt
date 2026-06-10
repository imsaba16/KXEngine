package com.developersyndicate.kxengine.debug

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class DebugGrid(
    size: Int = 10,
    spacing: Float = 1f
) {
    private val vaoId: Int
    private val vboId: Int
    private val vertexCount: Int

    init {
        val vertices = mutableListOf<Float>()

        val half = size * spacing

        // Vertical lines
        for (i in -size..size) {
            val x = i * spacing
            vertices += x; vertices += -half
            vertices += x; vertices += half
        }

        // Horizontal lines
        for (i in -size..size) {
            val y = i * spacing
            vertices += -half; vertices += y
            vertices += half; vertices += y
        }

        vertexCount = vertices.size / 2

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        val buffer = MemoryUtil.memAllocFloat(vertices.size)
        buffer.put(vertices.toFloatArray()).flip()

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)

        glVertexAttribPointer(
            0,
            2,
            GL_FLOAT,
            false,
            2 * Float.SIZE_BYTES,
            0
        )
        glEnableVertexAttribArray(0)

        MemoryUtil.memFree(buffer)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun draw() {
        glBindVertexArray(vaoId)
        glDrawArrays(GL_LINES, 0, vertexCount)
        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteBuffers(vboId)
        glDeleteVertexArrays(vaoId)
    }
}
