package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.graphics.Transform
import com.developersyndicate.kxengine.math.Vec2

class Collider(
    val transform: Transform,
    val halfSize: Vec2,
    var collisionLayer: Int = 1,
    var collisionMask: Int = -1 // 0xFFFFFFFF
) {
    fun aabb(): AABB {
        val p = transform.position
        return AABB(
            min = Vec2(p.x - halfSize.x, p.y - halfSize.y),
            max = Vec2(p.x + halfSize.x, p.y + halfSize.y)
        )
    }

    fun canCollideWith(other: Collider): Boolean {
        return (collisionLayer and other.collisionMask) != 0 &&
               (other.collisionLayer and collisionMask) != 0
    }
}