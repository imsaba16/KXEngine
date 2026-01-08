package com.developersyndicate.kxengine.graphics.shader

import com.developersyndicate.kxengine.graphics.Color
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil

open class Shader(
    vertexSource: String,
    fragmentSource: String
) {
    private val programId: Int

    init {
        val vertexId = compileShader(vertexSource, GL20.GL_VERTEX_SHADER)
        val fragmentId = compileShader(fragmentSource, GL20.GL_FRAGMENT_SHADER)

        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexId)
        GL20.glAttachShader(programId, fragmentId)
        GL20.glLinkProgram(programId)

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            error("Shader program linking failed:\n${GL20.glGetProgramInfoLog(programId)}")
        }

        GL20.glDeleteShader(vertexId)
        GL20.glDeleteShader(fragmentId)
    }

    private fun compileShader(source: String, type: Int): Int {
        val id = GL20.glCreateShader(type)
        GL20.glShaderSource(id, source)
        GL20.glCompileShader(id)

        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            error("Shader compilation failed:\n${GL20.glGetShaderInfoLog(id)}")
        }

        return id
    }

    fun setMat4(name: String, matrix: FloatArray) {
        val location = GL20.glGetUniformLocation(programId, name)
        val buffer = MemoryUtil.memAllocFloat(16)
        buffer.put(matrix).flip()
        GL20.glUniformMatrix4fv(location, false, buffer)
        MemoryUtil.memFree(buffer)
    }

    fun setVec4(name: String, color: Color) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform4f(location, color.r, color.g, color.b, color.a)
    }

    fun setInt(name: String, value: Int) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform1i(location, value)
    }

    fun bind() {
        GL20.glUseProgram(programId)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun destroy() {
        GL20.glDeleteProgram(programId)
    }
}