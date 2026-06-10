package com.developersyndicate.kxengine.graphics

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlesFullscreenQuad : Mesh {
    private val vaoId: Int
    private val vboId: Int

    init {
        // x, y, u, v
        // Coordinates range from -1.0 to 1.0 to cover the entire viewport
        val vertices = floatArrayOf(
            -1.0f,  1.0f,  0f, 1f, // top-left
            -1.0f, -1.0f,  0f, 0f, // bottom-left
            1.0f, -1.0f,  1f, 0f, // bottom-right

            -1.0f,  1.0f,  0f, 1f, // top-left
            1.0f, -1.0f,  1f, 0f, // bottom-right
            1.0f,  1.0f,  1f, 1f  // top-right
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

        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertices.size * 4,
            buffer,
            GLES30.GL_STATIC_DRAW
        )

        val stride = 4 * 4 // 4 floats * 4 bytes

        // position (location = 0)
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, stride, 0)
        GLES30.glEnableVertexAttribArray(0)

        // uv (location = 1)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, stride, 2 * 4)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    override fun bind() {
        GLES30.glBindVertexArray(vaoId)
    }

    override fun draw() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
    }

    override fun unbind() {
        GLES30.glBindVertexArray(0)
    }

    override fun destroy() {
        GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }
}
