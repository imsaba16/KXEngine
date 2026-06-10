package com.developersyndicate.kxengine.debug

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GlesDebugGrid(
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

        val temp = IntArray(1)
        GLES30.glGenVertexArrays(1, temp, 0)
        vaoId = temp[0]
        GLES30.glBindVertexArray(vaoId)

        GLES30.glGenBuffers(1, temp, 0)
        vboId = temp[0]
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        val floatArray = vertices.toFloatArray()
        val buffer = ByteBuffer.allocateDirect(floatArray.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(floatArray).position(0)

        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, floatArray.size * 4, buffer, GLES30.GL_STATIC_DRAW)

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
