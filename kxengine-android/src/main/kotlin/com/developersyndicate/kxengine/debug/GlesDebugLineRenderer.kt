package com.developersyndicate.kxengine.debug

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GlesDebugLineRenderer {
    private val vao: Int
    private val vbo: Int
    private val byteBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder())
    private val buffer: FloatBuffer = byteBuffer.asFloatBuffer()

    init {
        val temp = IntArray(1)
        GLES30.glGenVertexArrays(1, temp, 0)
        vao = temp[0]

        GLES30.glGenBuffers(1, temp, 0)
        vbo = temp[0]

        GLES30.glBindVertexArray(vao)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo)

        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 8 * 4, null, GLES30.GL_DYNAMIC_DRAW)

        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 2 * 4, 0)

        GLES30.glBindVertexArray(0)
    }

    fun drawRect(
        minX: Float,
        minY: Float,
        maxX: Float,
        maxY: Float
    ) {
        val vertices = floatArrayOf(
            minX, minY,
            maxX, minY,
            maxX, maxY,
            minX, maxY
        )

        buffer.clear()
        buffer.put(vertices).position(0)

        GLES30.glBindVertexArray(vao)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo)
        
        byteBuffer.position(0)
        byteBuffer.limit(vertices.size * 4)
        
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, byteBuffer.limit(), byteBuffer)
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 4)
        GLES30.glBindVertexArray(0)
    }

    fun destroy() {
        GLES30.glDeleteBuffers(1, intArrayOf(vbo), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(vao), 0)
    }
}
