package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.math.Vec2

data class Ray(val origin: Vec2, val direction: Vec2)

data class RaycastHit(
    val hit: Boolean,
    val point: Vec2,
    val normal: Vec2,
    val distance: Float,
    val collider: Collider?
) {
    companion object {
        val NONE = RaycastHit(false, Vec2(0f, 0f), Vec2(0f, 0f), Float.MAX_VALUE, null)
    }
}
