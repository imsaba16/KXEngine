package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.*

class Camera(
    width: Float,
    height: Float
) {
    private val projection = Mat4.orthographic(
        -width / 2f,
        width / 2f,
        -height / 2f,
        height / 2f
    )

    fun matrix(): Mat4 = projection
}