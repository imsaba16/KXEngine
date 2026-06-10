package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.Mat4

class UICamera(val width: Float, val height: Float) {
    val matrix: Mat4 = Mat4.orthographic(0f, width, 0f, height)
}
