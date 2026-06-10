package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.math.Vec2

data class AABB(
    val min: Vec2,
    val max: Vec2
) {
    fun overlaps(other: AABB): Boolean {
        return min.x < other.max.x &&
                max.x > other.min.x &&
                min.y < other.max.y &&
                max.y > other.min.y
    }
}