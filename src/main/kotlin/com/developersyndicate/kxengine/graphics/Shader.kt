package com.developersyndicate.kxengine.graphics

import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil

class Shader(
    vertexSource: String,
    fragmentSource: String
) {
    private val programId: Int

    init {
        val vertexId = compileShader(vertexSource, GL_VERTEX_SHADER)
        val fragmentId = compileShader(fragmentSource, GL_FRAGMENT_SHADER)

        programId = glCreateProgram()
        glAttachShader(programId, vertexId)
        glAttachShader(programId, fragmentId)
        glLinkProgram(programId)

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            error("Shader program linking failed:\n${glGetProgramInfoLog(programId)}")
        }

        glDeleteShader(vertexId)
        glDeleteShader(fragmentId)
    }

    private fun compileShader(source: String, type: Int): Int {
        val id = glCreateShader(type)
        glShaderSource(id, source)
        glCompileShader(id)

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            error("Shader compilation failed:\n${glGetShaderInfoLog(id)}")
        }

        return id
    }

    fun setMat4(name: String, matrix: FloatArray) {
        val location = glGetUniformLocation(programId, name)
        val buffer = MemoryUtil.memAllocFloat(16)
        buffer.put(matrix).flip()
        glUniformMatrix4fv(location, false, buffer)
        MemoryUtil.memFree(buffer)
    }

    fun setVec4(name: String, color: Color) {
        val location = glGetUniformLocation(programId, name)
        glUniform4f(location, color.r, color.g, color.b, color.a)
    }


    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun destroy() {
        glDeleteProgram(programId)
    }
}
