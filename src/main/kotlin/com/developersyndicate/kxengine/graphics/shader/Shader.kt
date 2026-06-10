package com.developersyndicate.kxengine.graphics.shader

import com.developersyndicate.kxengine.graphics.Color
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil
import java.io.File

open class Shader(
    vertexSource: String,
    fragmentSource: String
) {
    private var programId: Int = 0

    private var vertexFile: File? = null
    private var fragmentFile: File? = null
    private var vertLastModified = 0L
    private var fragLastModified = 0L

    init {
        compileAndLink(vertexSource, fragmentSource)
    }

    constructor(vertexPath: String, fragmentPath: String, isPath: Boolean = true) : this(
        readSource(vertexPath),
        readSource(fragmentPath)
    ) {
        val vf = File("src/main/resources/$vertexPath")
        if (vf.exists()) {
            vertexFile = vf
            vertLastModified = vf.lastModified()
        }
        val ff = File("src/main/resources/$fragmentPath")
        if (ff.exists()) {
            fragmentFile = ff
            fragLastModified = ff.lastModified()
        }
    }

    private fun compileAndLink(vertSrc: String, fragSrc: String) {
        val vertexId = compileShader(vertSrc, GL20.GL_VERTEX_SHADER)
        val fragmentId = compileShader(fragSrc, GL20.GL_FRAGMENT_SHADER)

        val newProgramId = GL20.glCreateProgram()
        GL20.glAttachShader(newProgramId, vertexId)
        GL20.glAttachShader(newProgramId, fragmentId)
        GL20.glLinkProgram(newProgramId)

        if (GL20.glGetProgrami(newProgramId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetProgramInfoLog(newProgramId)
            GL20.glDeleteShader(vertexId)
            GL20.glDeleteShader(fragmentId)
            GL20.glDeleteProgram(newProgramId)
            error("Shader program linking failed:\n$log")
        }

        GL20.glDeleteShader(vertexId)
        GL20.glDeleteShader(fragmentId)

        if (programId != 0) {
            GL20.glDeleteProgram(programId)
        }
        programId = newProgramId
    }

    private fun compileShader(source: String, type: Int): Int {
        val id = GL20.glCreateShader(type)
        GL20.glShaderSource(id, source)
        GL20.glCompileShader(id)

        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetShaderInfoLog(id)
            GL20.glDeleteShader(id)
            error("Shader compilation failed:\n$log")
        }

        return id
    }

    fun checkAndReload(): Boolean {
        val vf = vertexFile ?: return false
        val ff = fragmentFile ?: return false
        if (vf.exists() && ff.exists()) {
            val vMod = vf.lastModified()
            val fMod = ff.lastModified()
            if (vMod > vertLastModified || fMod > fragLastModified) {
                vertLastModified = vMod
                fragLastModified = fMod
                try {
                    compileAndLink(vf.readText(), ff.readText())
                    println("SoundEngine/AssetSystem: Shader reloaded successfully.")
                    return true
                } catch (e: Exception) {
                    println("Warning: Error reloading shader: ${e.message}")
                }
            }
        }
        return false
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

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform3f(location, x, y, z)
    }

    fun setVec3(name: String, color: Color) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform3f(location, color.r, color.g, color.b)
    }

    fun setInt(name: String, value: Int) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform1i(location, value)
    }

    fun setFloat(name: String, value: Float) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform1f(location, value)
    }

    fun bind() {
        GL20.glUseProgram(programId)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun destroy() {
        if (programId != 0) {
            GL20.glDeleteProgram(programId)
            programId = 0
        }
    }

    companion object {
        private fun readSource(path: String): String {
            val f = File("src/main/resources/$path")
            if (f.exists()) return f.readText()
            val stream = Shader::class.java.classLoader.getResourceAsStream(path)
                ?: error("Shader file not found: $path")
            return stream.bufferedReader().use { it.readText() }
        }
    }
}