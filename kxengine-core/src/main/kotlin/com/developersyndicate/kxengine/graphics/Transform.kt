package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.Mat4
import com.developersyndicate.kxengine.math.Vec3

class Transform {
    var position = Vec3.ZERO
    var rotation = 0f
    var scale = Vec3.ONE

    var parent: Transform? = null

    val worldPosition: Vec3
        get() {
            if (parent != null) {
                val m = matrix().toFloatArray()
                return Vec3(m[12], m[13], m[14])
            }
            return position
        }

    val worldScale: Vec3
        get() {
            if (parent != null) {
                val m = matrix().toFloatArray()
                val sx = Math.sqrt((m[0]*m[0] + m[1]*m[1] + m[2]*m[2]).toDouble()).toFloat()
                val sy = Math.sqrt((m[4]*m[4] + m[5]*m[5] + m[6]*m[6]).toDouble()).toFloat()
                val sz = Math.sqrt((m[8]*m[8] + m[9]*m[9] + m[10]*m[10]).toDouble()).toFloat()
                return Vec3(sx, sy, sz)
            }
            return scale
        }

    fun matrix(): Mat4 {
        val local = Mat4.translation(position) *
                Mat4.rotationZ(rotation) *
                Mat4.scale(scale)
        val p = parent
        return if (p != null) {
            p.matrix() * local
        } else {
            local
        }
    }
}