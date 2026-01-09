package com.developersyndicate.kxengine.physics

import com.developersyndicate.kxengine.graphics.Transform
import com.developersyndicate.kxengine.math.Vec2

class Collider(
    private val transform: Transform,
    private val halfSize: Vec2
) {
    fun aabb(): AABB {
        val p = transform.position
        return AABB(
            min = Vec2(p.x - halfSize.x, p.y - halfSize.y),
            max = Vec2(p.x + halfSize.x, p.y + halfSize.y)
        )
    }
}