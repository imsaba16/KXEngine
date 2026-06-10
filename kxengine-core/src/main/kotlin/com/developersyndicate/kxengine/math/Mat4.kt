package com.developersyndicate.kxengine.math

import kotlin.math.cos
import kotlin.math.sin

class Mat4(private val data: FloatArray = FloatArray(16)) {
    operator fun times(other: Mat4) : Mat4 {
        val result = FloatArray(16)
        for (row in 0..3) {
            for (col in 0..3) {
                for (i in 0..3) {
                    result[col * 4 + row] +=
                        data[i * 4 + row] * other.data[col * 4 + i]
                }
            }
        }
        return Mat4(result)
    }

    fun toFloatArray(): FloatArray = data

    companion object {

        fun identity(): Mat4 {
            val m = FloatArray(16)
            m[0] = 1f
            m[5] = 1f
            m[10] = 1f
            m[15] = 1f
            return Mat4(m)
        }

        fun translation(v: Vec3): Mat4 {
            val m = identity().data
            m[12] = v.x
            m[13] = v.y
            m[14] = v.z
            return Mat4(m)
        }

        fun scale(v: Vec3): Mat4 {
            val m = identity().data
            m[0] = v.x
            m[5] = v.y
            m[10] = v.z
            return Mat4(m)
        }

        fun rotationZ(angle: Float): Mat4 {
            val m = identity().data
            val c = cos(angle)
            val s = sin(angle)

            m[0] = c
            m[1] = s
            m[4] = -s
            m[5] = c
            return Mat4(m)
        }

        fun orthographic(
            left: Float,
            right: Float,
            bottom: Float,
            top: Float
        ): Mat4 {
            val m = FloatArray(16)
            m[0] = 2f / (right - left)
            m[5] = 2f / (top - bottom)
            m[10] = -1f
            m[12] = -(right + left) / (right - left)
            m[13] = -(top + bottom) / (top - bottom)
            m[15] = 1f
            return Mat4(m)
        }
    }
}