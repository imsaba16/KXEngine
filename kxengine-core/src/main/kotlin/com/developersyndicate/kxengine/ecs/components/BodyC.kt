package com.developersyndicate.kxengine.ecs.components

import com.developersyndicate.kxengine.ecs.Component
import com.developersyndicate.kxengine.math.Vec2

data class BodyC(
    var velocity: Vec2 = Vec2(0f, 0f),
    var grounded: Boolean = false
) : Component