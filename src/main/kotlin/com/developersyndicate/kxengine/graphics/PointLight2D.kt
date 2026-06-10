package com.developersyndicate.kxengine.graphics

import com.developersyndicate.kxengine.math.Vec2

data class PointLight2D(
    var position: Vec2,
    var color: Color = Color.WHITE,
    var radius: Float = 4.0f,
    var intensity: Float = 1.0f
)
