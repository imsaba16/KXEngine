package com.developersyndicate.kxengine.graphics.shader

import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.platform.KXPlatform
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil

open class LwjglShader(
    vertexSource: String,
    fragmentSource: String
) : Shader {
    private var programId: Int = 0

    private var vertexPath: String = ""
    private var fragmentPath: String = ""
    private var vertLastModified = 0L
    private var fragLastModified = 0L

    init {
        compileAndLink(vertexSource, fragmentSource)
    }

    constructor(vPath: String, fPath: String, isPath: Boolean = true) : this(
        KXPlatform.fileSystem.readText(vPath),
        KXPlatform.fileSystem.readText(fPath)
    ) {
        vertexPath = vPath
        fragmentPath = fPath
        vertLastModified = KXPlatform.fileSystem.lastModified(vPath)
        fragLastModified = KXPlatform.fileSystem.lastModified(fPath)
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

    override fun checkAndReload(): Boolean {
        if (vertexPath.isEmpty() || fragmentPath.isEmpty()) return false
        val vMod = KXPlatform.fileSystem.lastModified(vertexPath)
        val fMod = KXPlatform.fileSystem.lastModified(fragmentPath)
        if (vMod > vertLastModified || fMod > fragLastModified) {
            vertLastModified = vMod
            fragLastModified = fMod
            try {
                compileAndLink(KXPlatform.fileSystem.readText(vertexPath), KXPlatform.fileSystem.readText(fragmentPath))
                println("AssetSystem: Shader reloaded successfully.")
                return true
            } catch (e: Exception) {
                println("Warning: Error reloading shader: ${e.message}")
            }
        }
        return false
    }

    override fun setMat4(name: String, matrix: FloatArray) {
        val location = GL20.glGetUniformLocation(programId, name)
        val buffer = MemoryUtil.memAllocFloat(16)
        buffer.put(matrix).flip()
        GL20.glUniformMatrix4fv(location, false, buffer)
        MemoryUtil.memFree(buffer)
    }

    override fun setVec4(name: String, color: Color) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform4f(location, color.r, color.g, color.b, color.a)
    }

    override fun setVec3(name: String, x: Float, y: Float, z: Float) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform3f(location, x, y, z)
    }

    override fun setVec3(name: String, color: Color) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform3f(location, color.r, color.g, color.b)
    }

    override fun setInt(name: String, value: Int) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform1i(location, value)
    }

    override fun setFloat(name: String, value: Float) {
        val location = GL20.glGetUniformLocation(programId, name)
        GL20.glUniform1f(location, value)
    }

    override fun bind() {
        GL20.glUseProgram(programId)
    }

    override fun unbind() {
        GL20.glUseProgram(0)
    }

    override fun destroy() {
        if (programId != 0) {
            GL20.glDeleteProgram(programId)
            programId = 0
        }
    }
}
