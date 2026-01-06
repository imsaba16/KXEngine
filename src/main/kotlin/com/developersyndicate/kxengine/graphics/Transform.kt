package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.Mat4
import com.developersyndicate.kxengine.math.Vec3

class Transform {
    var position = Vec3.ZERO
    var rotation = 0f
    var scale = Vec3.ONE

    fun matrix(): Mat4 {
        return Mat4.translation(position) *
                Mat4.rotationZ(rotation) *
                Mat4.scale(scale)
    }
}