package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.math.Vec2

class Body(
    var velocity: Vec2 = Vec2(0f, 0f),
    var gravityScale: Float = 1f,
    var grounded: Boolean = false
)