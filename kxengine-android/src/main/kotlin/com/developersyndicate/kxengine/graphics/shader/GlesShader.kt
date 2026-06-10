package com.developersyndicate.kxengine.graphics.shader

import android.opengl.GLES30
import com.developersyndicate.kxengine.graphics.Color
import com.developersyndicate.kxengine.platform.KXPlatform
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class GlesShader(
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
        val convertedVert = convertShaderSource(vertSrc, isFragment = false)
        val convertedFrag = convertShaderSource(fragSrc, isFragment = true)

        val vertexId = compileShader(convertedVert, GLES30.GL_VERTEX_SHADER)
        val fragmentId = compileShader(convertedFrag, GLES30.GL_FRAGMENT_SHADER)

        val newProgramId = GLES30.glCreateProgram()
        GLES30.glAttachShader(newProgramId, vertexId)
        GLES30.glAttachShader(newProgramId, fragmentId)
        GLES30.glLinkProgram(newProgramId)

        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(newProgramId, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == GLES30.GL_FALSE) {
            val log = GLES30.glGetProgramInfoLog(newProgramId)
            GLES30.glDeleteShader(vertexId)
            GLES30.glDeleteShader(fragmentId)
            GLES30.glDeleteProgram(newProgramId)
            error("Shader program linking failed:\n$log")
        }

        GLES30.glDeleteShader(vertexId)
        GLES30.glDeleteShader(fragmentId)

        if (programId != 0) {
            GLES30.glDeleteProgram(programId)
        }
        programId = newProgramId
    }

    private fun compileShader(source: String, type: Int): Int {
        val id = GLES30.glCreateShader(type)
        GLES30.glShaderSource(id, source)
        GLES30.glCompileShader(id)

        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == GLES30.GL_FALSE) {
            val log = GLES30.glGetShaderInfoLog(id)
            GLES30.glDeleteShader(id)
            error("Shader compilation failed for type $type:\n$log\nSource:\n$source")
        }

        return id
    }

    private fun convertShaderSource(source: String, isFragment: Boolean): String {
        if (!source.contains("#version 330 core")) return source

        var converted = source.replace("#version 330 core", "#version 300 es")
        val index = converted.indexOf("#version 300 es")
        if (index != -1) {
            val endOfLine = converted.indexOf("\n", index)
            val precision = if (isFragment) "precision mediump float;\n" else "precision highp float;\n"
            converted = converted.substring(0, endOfLine + 1) + precision + converted.substring(endOfLine + 1)
        }
        return converted
    }

    override fun checkAndReload(): Boolean {
        if (vertexPath.isEmpty() || fragmentPath.isEmpty()) return false
        val vMod = KXPlatform.fileSystem.lastModified(vertexPath)
        val fMod = KXPlatform.fileSystem.lastModified(fragmentPath)
        if (vMod > vertLastModified || fMod > fragLastModified) {
            vertLastModified = vMod
            fragLastModified = fMod
            try {
                compileAndLink(
                    KXPlatform.fileSystem.readText(vertexPath),
                    KXPlatform.fileSystem.readText(fragmentPath)
                )
                println("AssetSystem: Shader reloaded successfully.")
                return true
            } catch (e: Exception) {
                println("Warning: Error reloading shader: ${e.message}")
            }
        }
        return false
    }

    override fun setMat4(name: String, matrix: FloatArray) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0)
    }

    override fun setVec4(name: String, color: Color) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniform4f(location, color.r, color.g, color.b, color.a)
    }

    override fun setVec3(name: String, x: Float, y: Float, z: Float) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniform3f(location, x, y, z)
    }

    override fun setVec3(name: String, color: Color) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniform3f(location, color.r, color.g, color.b)
    }

    override fun setInt(name: String, value: Int) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniform1i(location, value)
    }

    override fun setFloat(name: String, value: Float) {
        val location = GLES30.glGetUniformLocation(programId, name)
        GLES30.glUniform1f(location, value)
    }

    override fun bind() {
        GLES30.glUseProgram(programId)
    }

    override fun unbind() {
        GLES30.glUseProgram(0)
    }

    override fun destroy() {
        if (programId != 0) {
            GLES30.glDeleteProgram(programId)
            programId = 0
        }
    }
}
