package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.math.Vec2

data class CollisionResult(
    val collided: Boolean,
    val time: Float,
    val normal: Vec2
) {
    companion object {
        val NONE = CollisionResult(false, 1.0f, Vec2(0f, 0f))
    }
}

data class ResolutionResult(
    val velocity: Vec2,
    val grounded: Boolean,
    val hitWall: Boolean
)
