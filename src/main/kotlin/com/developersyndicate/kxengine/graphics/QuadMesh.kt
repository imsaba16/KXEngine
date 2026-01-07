package com.developersyndicate.kxengine.graphics

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class QuadMesh {

    private val vaoId: Int
    private val vboId: Int

    init {
        // x, y, u, v
        val vertices = floatArrayOf(
            -0.5f,  0.5f,  0f, 1f, // top-left
            -0.5f, -0.5f,  0f, 0f, // bottom-left
            0.5f, -0.5f,  1f, 0f, // bottom-right

            -0.5f,  0.5f,  0f, 1f, // top-left
            0.5f, -0.5f,  1f, 0f, // bottom-right
            0.5f,  0.5f,  1f, 1f  // top-right
        )

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        val buffer = MemoryUtil.memAllocFloat(vertices.size)
        buffer.put(vertices).flip()

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)

        val stride = 4 * Float.SIZE_BYTES

        // position
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0)
        glEnableVertexAttribArray(0)

        // uv
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(1)

        MemoryUtil.memFree(buffer)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun bind() {
        glBindVertexArray(vaoId)
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLES, 0, 6)
    }

    fun unbind() {
        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteBuffers(vboId)
        glDeleteVertexArrays(vaoId)
    }
}
