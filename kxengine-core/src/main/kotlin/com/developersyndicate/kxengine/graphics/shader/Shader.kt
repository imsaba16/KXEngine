package com.developersyndicate.kxengine.graphics.shader

import com.developersyndicate.kxengine.graphics.Color

interface Shader {
    fun setMat4(name: String, matrix: FloatArray)
    fun setVec4(name: String, color: Color)
    fun setVec3(name: String, x: Float, y: Float, z: Float)
    fun setVec3(name: String, color: Color)
    fun setInt(name: String, value: Int)
    fun setFloat(name: String, value: Float)
    fun bind()
    fun unbind()
    fun destroy()
    fun checkAndReload(): Boolean = false
}
