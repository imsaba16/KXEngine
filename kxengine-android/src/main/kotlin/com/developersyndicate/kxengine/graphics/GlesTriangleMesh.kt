package com.developersyndicate.kxengine.graphics

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GlesTriangleMesh : Mesh {
    private val vaoId: Int
    private val vboId: Int

    init {
        val vertices = floatArrayOf(
            0.0f,  0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
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

        // position (location = 0)
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

    override fun bind() {
        GLES30.glBindVertexArray(vaoId)
    }

    override fun unbind() {
        GLES30.glBindVertexArray(0)
    }

    override fun draw() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
    }

    override fun destroy() {
        GLES30.glDeleteBuffers(1, intArrayOf(vboId), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }
}
