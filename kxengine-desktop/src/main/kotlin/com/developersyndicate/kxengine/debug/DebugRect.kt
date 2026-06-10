package com.developersyndicate.kxengine.debug

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

class DebugRect(
    width: Float,
    height: Float
) {
    private val vaoId: Int
    private val vboId: Int
    private val vertexCount: Int = 8

    init {
        val hw = width / 2f
        val hh = height / 2f

        val vertices = floatArrayOf(
            -hw, -hh,   hw, -hh,
            hw, -hh,   hw,  hh,
            hw,  hh,  -hw,  hh,
            -hw,  hh,  -hw, -hh
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
