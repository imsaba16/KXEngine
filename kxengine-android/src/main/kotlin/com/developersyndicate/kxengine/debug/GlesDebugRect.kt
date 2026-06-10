package com.developersyndicate.kxengine.debug

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlesDebugRect(
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

        val temp = IntArray(1)
        GLES30.glGenVertexArrays(1, temp, 0)
        vaoId = temp[0]
        GLES30.glBindVertexArray(vaoId)

        GLES30.glGenBuffers(1, temp, 0)
        vboId = temp[0]
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertices).position(0)

        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.size * 4, buffer, GLES30.GL_STATIC_DRAW)

        GLES30.glVertexAttribPointer(
            0,
            2,
            GLES30.GL_FLOAT,
            false,
            2 * 4,
            0
        )
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    fun draw() {
        GLES30.glBindVertexArray(vaoId)
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexCount)
        GLES30.glBindVertexArray(0)
    }

    fun destroy() {
        GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }
}
