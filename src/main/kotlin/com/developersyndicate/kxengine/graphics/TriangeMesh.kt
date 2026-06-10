package com.developersyndicate.kxengine.graphics

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class TriangleMesh : Mesh {

    private val vaoId: Int
    private val vboId: Int

    init {
        val vertices = floatArrayOf(
            0.0f,  0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        )

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        val buffer = MemoryUtil.memAllocFloat(vertices.size)
        buffer.put(vertices).flip()

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

    override fun bind() {
        glBindVertexArray(vaoId)
    }

    override fun unbind() {
        glBindVertexArray(0)
    }

    override fun draw() {
        glDrawArrays(GL_TRIANGLES, 0, 3)
    }

    override fun destroy() {
        glDeleteBuffers(vboId)
        glDeleteVertexArrays(vaoId)
    }
}
