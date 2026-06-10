package com.developersyndicate.kxengine.debug

import com.developersyndicate.kxengine.graphics.shader.Shader
import com.developersyndicate.kxengine.math.Mat4
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

class DebugLineRenderer {

    private val vao = glGenVertexArrays()
    private val vbo = glGenBuffers()

    init {
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)

        glBufferData(GL_ARRAY_BUFFER, 8 * 4L, GL_DYNAMIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0)

        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)

        glBindVertexArray(0)
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

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)
        glDrawArrays(GL_LINE_LOOP, 0, 4)
        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteBuffers(vbo)
        glDeleteVertexArrays(vao)
    }
}